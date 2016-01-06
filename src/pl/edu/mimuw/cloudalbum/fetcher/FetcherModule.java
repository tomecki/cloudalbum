package pl.edu.mimuw.cloudalbum.fetcher;

import com.sun.management.OperatingSystemMXBean;
import pl.edu.mimuw.cloudalbum.interfaces.Fetcher;
import pl.edu.mimuw.cloudatlas.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tomek on 08.12.15.
 */
public class FetcherModule implements Fetcher {
    private static Logger logger = Logger.getLogger(FetcherModule.class.getName());
    public AttributesMap getLocalStats() throws RemoteException {
        logger.log(Level.INFO, "Retrieving local stats");

        AttributesMap result = new AttributesMap();
        try{
            result.add(new Attribute("hostname"), new ValueString(InetAddress.getLocalHost().getHostName()));
        } catch(Exception e){ }
        try{
            result.add(new Attribute("num_cores"), new ValueInt((long) Runtime.getRuntime().availableProcessors()));
        } catch(Exception e) {}
        try{
            OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            try {
                result.add(new Attribute("cpu_load"), new ValueDouble(operatingSystemMXBean.getSystemCpuLoad()));
            } catch(Exception e) {}
            try{
                result.add(new Attribute("free_disk"), new ValueInt(operatingSystemMXBean.getFreePhysicalMemorySize()));
            } catch(Exception e) {}
            result.add(new Attribute("free_swap"), new ValueInt(operatingSystemMXBean.getFreeSwapSpaceSize()));
            result.add(new Attribute("total_swap"), new ValueInt(operatingSystemMXBean.getTotalSwapSpaceSize()));
        } catch(Exception e){}
        try{
            result.add(new Attribute("total_disk"), new ValueInt(new File("/").getTotalSpace()));
            result.add(new Attribute("total_ram"), new ValueInt(Runtime.getRuntime().totalMemory()));
            result.add(new Attribute("free_ram"), new ValueInt(Runtime.getRuntime().freeMemory()));

            result.add(new Attribute("num_processes"), new ValueInt(getProcessesCount()));
            result.add(new Attribute("kernel_ver"), new ValueString(getKernelVersion()));
            result.add(new Attribute("logged_users"), new ValueInt(getLoggedUsers()));
            HashSet<Value> hs = new HashSet<Value>();
            hs.add(new ValueString(InetAddress.getLocalHost().getHostName()));
            // TODO: more host names
            result.add(new Attribute("dns_names"), new ValueSet(hs, new ValueString("").getType()));
        } catch (Exception e) {}
        // Serialization test
        List<Value> l = new ArrayList<>();
        l.add(new ValueDuration(5l));
        l.add(new ValueDuration(6l));
        l.add(new ValueDuration(7l));
        result.add(new Attribute("fajna_lista"), new ValueList(l, l.get(0).getType()));
        //
        result.add(new Attribute("null_wartosc"), ValueNull.getInstance());
        //
        try {
            result.add(new Attribute("path_nazwa"), new ValueContact(new PathName("/localhost"), InetAddress.getByAddress(new byte[] {1,2,3,4
            })));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Returning stats: " + result.toString());
        return result;
    }

    private Long getLoggedUsers() throws IOException {
        Process p = Runtime.getRuntime().exec("users");
        String line;

        BufferedReader input =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
        line = input.readLine();
        input.close();
        return (long)line.split(" ").length;
    }

    private String getKernelVersion() throws IOException {
        Process p = Runtime.getRuntime().exec("uname -r");
        String line;

        BufferedReader input =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
        line = input.readLine();
        input.close();
        return line;
    }

    private Long getProcessesCount() throws IOException {
        String line;
        Process p = Runtime.getRuntime().exec("ps -e");
        BufferedReader input =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
        int i = 0;
        while ((line = input.readLine()) != null) {
            System.out.println(line); //<-- Parse data here.
            ++i;
        }
        input.close();
        return (long)i;
    }


    public static void main(String args[]){
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "FetcherModule";
            FetcherModule engine = new FetcherModule();
            Fetcher stub =
                    (Fetcher) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry("localhost", Integer.parseInt(args[0]));
            registry.rebind(name, stub);
            System.out.println("ComputeEngine bound");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }
    }

}
