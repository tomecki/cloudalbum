package pl.edu.mimuw.cloudalbum.agent;

import pl.edu.mimuw.cloudalbum.eda.Dispatcher;
import pl.edu.mimuw.cloudalbum.interfaces.Fetcher;
import pl.edu.mimuw.cloudatlas.model.*;

import java.io.*;
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
    private static class FetcherUpdater implements Runnable {
        private ZMI zmi;
        private Fetcher fetcher;
        private AttributesMap currentState;

        public FetcherUpdater(Fetcher fetcher, ZMI zmi) {
            this.fetcher = fetcher; this.zmi = zmi;
        }

        @Override
        public void run() {
            long delay = Long.parseLong(Agent.configuration.get("fetcherFrequency"));
            for(;;){
                synchronized(this){
                    currentState = Agent.getLocalStats(fetcher);
                    zmi.getAttributes().addOrChange(currentState);
                }
                logger.log(Level.INFO, "Local stats fetched");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Map<String, String> configuration = new HashMap<>();
    public static void main(String args[]){
        readConfiguration(args.length==0?"settings.conf" : args[1]);
        ExecutorService ex = Executors.newFixedThreadPool(2);
        ZMI zmi = new ZMI();
        ZMI root = createZMIHierarchy(configuration.get("path"));
        logger.log(Level.INFO, root.toString());
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", Integer.parseInt(args[0]));
            Fetcher fetcher = (Fetcher) registry.lookup("FetcherModule");
            ex.submit(new FetcherUpdater(fetcher, zmi));

        } catch (Exception e) {
            System.err.println("FibonacciClient exception:");
            e.printStackTrace();
        }
    }

    private static ZMI createZMIHierarchy(String arg) {
        String[] path = arg.split("/");
        ZMI root = new ZMI();
//        ZMI absoluteRoot = root;
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

    private static void readConfiguration(String arg) {
        try {
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
    private static AttributesMap getLocalStats(Fetcher fetcher){
        AttributesMap am = null;
        try {
            logger.log(Level.INFO, "Fetching local stats " + fetcher.getClass().toString());
            am = fetcher.getLocalStats();
            logger.log(Level.INFO, "Local stats fetched: "+ am.toString());

            Iterator<Map.Entry<Attribute, Value>> it;
            it = am.iterator();
            if(am!=null) {
                while (it.hasNext()) {
                    Map.Entry<Attribute, Value> e = it.next();
                    System.out.println(e.getKey().getName() + " " + e.getValue().toString());
                }
            }
            return am;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;

    }
}
