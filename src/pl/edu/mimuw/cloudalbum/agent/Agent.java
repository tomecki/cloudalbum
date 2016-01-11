package pl.edu.mimuw.cloudalbum.agent;

import com.sun.deploy.util.ArrayUtil;
import pl.edu.mimuw.cloudalbum.eda.Dispatcher;
import pl.edu.mimuw.cloudalbum.interfaces.Fetcher;
import pl.edu.mimuw.cloudalbum.interfaces.QuerySigner;
import pl.edu.mimuw.cloudatlas.interpreter.Main;
import pl.edu.mimuw.cloudatlas.model.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tomek on 20.12.15.
 */
public class Agent {
    private static Logger logger = Logger.getLogger(String.valueOf(Agent.class));
    private static Hashtable<String, ValueContact> addresses = new Hashtable<>();
    public static ZMI zmi;
    public static Map<String, String> configuration = new HashMap<>();
    public static void main(String args[]){
        readConfiguration(args.length==0?"settings.conf" : args[1]);
        zmi = createZMIHierarchy(configuration.get("path"));
        fillContacts(zmi, configuration);
        logger.log(Level.INFO, "Configuration finished: "+ zmi.toString()+ ": "+ zmi.getAttributes().toString());
        ExecutorService ex = Executors.newFixedThreadPool(4);
        try {
            // ================================ Local fetcher instance
            logger.log(Level.INFO, "Binding to registry");
            Registry registry = LocateRegistry.getRegistry("localhost", Integer.parseInt(args[0]));
            logger.log(Level.INFO, "Registry found: "+ registry.toString());
            Fetcher fetcher = (Fetcher) registry.lookup("FetcherModule");
            logger.log(Level.INFO, "Stub looked up");
            ex.execute(new FetcherUpdater(fetcher, zmi));
            logger.log(Level.INFO, "FetcherUpdater thread submitted");

            // ================================= Query Signer
            logger.log(Level.INFO, "QS Binding to registry");
            Registry qsRegistry = LocateRegistry.getRegistry(((ValueContact)(zmi.getAttributes().get("querySigner"))).getAddress().getHostName(), Integer.parseInt(args[0]));
            logger.log(Level.INFO, "QS Registry found: "+ qsRegistry.toString());
            QuerySigner querySigner = (QuerySigner) qsRegistry.lookup("QuerySignerModule");
            logger.log(Level.INFO, "QS Stub looked up");
            ex.execute(new AgentUpdater(querySigner));
            logger.log(Level.INFO, "AgentUpdater thread submitted");


        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void fillContacts(ZMI root, Map<String, String> configuration) {
        try{
            String[] querySigner = configuration.get("querySigner").split(",");
            root.getAttributes().addOrChange("querySigner", new ValueContact(new PathName(querySigner[0]), InetAddress.getByName(querySigner[1])));
            String[] contacts = configuration.get("contacts").split("#");
            root = root.getFather();
            for(String level: contacts){
                String[] levelContacts = (level.split(">")[1]).split(",");
                Set<Value> set = new HashSet<>();
                logger.log(Level.INFO, ArrayUtil.arrayToString(levelContacts));
                for(String contact: levelContacts){
                    if(addresses.containsKey(contact))
                        set.add(addresses.get(contact));
                }
                logger.log(Level.INFO, "Adding contacts for level: "+ root.getAttributes().get("name")+ " -> " + set.toString());
                root.getAttributes().addOrChange("contacts", new ValueSet(set, TypePrimitive.CONTACT));
                root = root.getFather();
            }
        } catch(Exception e){
            logger.log(Level.SEVERE, "Error creating contacts configuration from file!");
            e.printStackTrace();
        }
    }

    public static ZMI createZMIHierarchy(String arg) {
        String[] path = arg.split("/");
        ZMI root = new ZMI();
        root.getAttributes().add("name", new ValueString(path[1]));
        for(int i = 2; i<path.length; ++i){
            ZMI next = new ZMI();
            next.getAttributes().add("name", new ValueString(path[i]));
            root.addSon(next);
            next.setFather(root);
            root = next;
        }
        return root;
    }

    public static void readConfiguration(String arg) {
        try {
            logger.log(Level.INFO, "Reading selectedHosts addreses");
            List<String> addr = Files.readAllLines(Paths.get("selectedHosts"), Charset.defaultCharset());
            for (String a: addr){
                String[] as = a.split(",");
                addresses.put(new PathName(as[0]).getSingletonName(), getAddrFromString(as[0], as[1]));
                logger.log(Level.INFO, "Read host: "+ addresses.get(new PathName(as[0]).getName()));
            }

            logger.log(Level.INFO, "Reading configuration from path: "+ arg);
            List<String> lines = Files.readAllLines(Paths.get(arg), Charset.defaultCharset());
            for(String line: lines){
                String[] arr = line.split(":");
                logger.log(Level.INFO, "Reading configuration entry: " + line);
                configuration.put(arr[0], arr[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ValueContact getAddrFromString(String path, String a) {
        logger.log(Level.INFO, "PATH: " + path + " A: "+a);

        ValueContact r = null;
        try {
            r = Main.createContact(path, a);
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Host "+ a + "down" + e.getMessage());
        }
        return r;
    }

}
