package pl.edu.mimuw.cloudalbum.agent;

import pl.edu.mimuw.cloudalbum.contracts.GetZMIContract;
import pl.edu.mimuw.cloudalbum.contracts.InstallQueryContract;
import pl.edu.mimuw.cloudalbum.contracts.StatusContract;
import pl.edu.mimuw.cloudalbum.contracts.ZMIContract;
import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudalbum.interfaces.Fetcher;
import pl.edu.mimuw.cloudalbum.interfaces.GossipingAgent;
import pl.edu.mimuw.cloudalbum.interfaces.QuerySigner;
import pl.edu.mimuw.cloudalbum.querysigner.QuerySignerModule;
import pl.edu.mimuw.cloudatlas.interpreter.Main;
import pl.edu.mimuw.cloudatlas.model.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tomek on 20.12.15.
 */
public class Agent implements GossipingAgent {

    private static Logger logger = Logger.getLogger(String.valueOf(Agent.class));
    private static Hashtable<String, ValueContact> addresses = new Hashtable<>();
    /**
     * The root of Agent's ZMI structure
     */
    public static ZMI zmi;
    private static Hashtable<String, String> queries = new Hashtable<>();

    public static ZMI getMyZMI() { return zmi.getZoneOrNull(myPath); }

    public static PathName getMyPath() {
        return myPath;
    }

    public static void setMyPath(PathName myPath) {
        Agent.myPath = myPath;
    }

    private static PathName myPath;

    public static long lastZMIupdate = 0;
    public static Map<String, String> configuration = new HashMap<>();
    public static long offset = 0;
    public static long getCurrentTime() { return System.currentTimeMillis() + offset; }


    public static QuerySigner querySigner;

    public static void main(String args[]){
        readConfiguration(args.length==0?"settings.conf" : args[1]);
        zmi = createZMIHierarchy(configuration.get("allNodes"), configuration.get("path"));
        installQueries(zmi, queries);
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

            logger.log(Level.INFO, "FetcherUpdater thread submitted");

            // ================================= Agent module binding
            // TODO

            Agent agent = new Agent();
            GossipingAgent stub = (GossipingAgent) UnicastRemoteObject.exportObject(agent,  0);
            registry.rebind("AgentModule", stub);

            // ================================= Query Signer
            logger.log(Level.INFO, "QS Binding to registry");
            Registry qsRegistry = LocateRegistry.getRegistry(((ValueContact)(Agent.getMyZMI().getAttributes().get("querySigner"))).getAddress().getHostName(), Integer.parseInt(args[0]));
            logger.log(Level.INFO, "QS Registry found: "+ qsRegistry.toString());
            querySigner = (QuerySigner) qsRegistry.lookup("QuerySignerModule");
            logger.log(Level.INFO, "QS Stub looked up");
            ex.execute(new AgentUpdater(querySigner));
            logger.log(Level.INFO, "AgentUpdater thread submitted");

            ex.execute(new FetcherUpdater(fetcher, Agent.zmi));
            ex.execute(new QueryUpdater());


        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void installQueries(ZMI zmi, Hashtable<String, String> queries) {
        if(zmi.getSons() != null && zmi.getSons().size() > 0) {
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                zmi.getAttributes().addOrChange(entry.getKey(), new ValueString(entry.getValue()));
                zmi.getFreshness().addOrChange(entry.getKey(), new ValueDuration(getCurrentTime()));
            }
            for (ZMI son : zmi.getSons()) {
                installQueries(son, queries);
            }
        }
    }

    public static void fillContacts(ZMI root, Map<String, String> configuration) {
        try{
            logger.info("configuration: "+ configuration.toString());
            String[] querySigner = configuration.get("querySigner").split(",");
            Agent.getMyZMI().getAttributes().addOrChange("querySigner", new ValueContact(new PathName(querySigner[0]), InetAddress.getByName(querySigner[1])));
            Agent.getMyZMI().getFreshness().addOrChange("querySigner", new ValueDuration(getCurrentTime()));
            String[] contacts = configuration.get("contacts").split("#");
            ZMI myZone = root.getZoneOrNull(new PathName(configuration.get("path")));
            ZMI iterator =  myZone.getFather();
            for(String level: contacts){
                String[] levelContacts = (level.split(">")[1]).split(",");
                Set<Value> set = new TreeSet<>();
                logger.log(Level.INFO, Arrays.toString(levelContacts));
                for(String contact: levelContacts){
                    if(addresses.containsKey(contact))
                        set.add(addresses.get(contact));
                }
                logger.log(Level.INFO, "Adding contacts for level: "+ iterator.getPathName()+ " -> " + set.toString());
                iterator.getAttributes().addOrChange("contacts", new ValueSet(set, TypePrimitive.CONTACT));
                iterator.getFreshness().addOrChange("contacts", new ValueDuration(getCurrentTime()));
                iterator = iterator.getFather();
            }
        } catch(Exception e){
            logger.log(Level.SEVERE, "Error creating contacts configuration from file!");
            e.printStackTrace();
        }
    }

    public static ZMI createZMIHierarchy(String arg, String path) {

        String[] nodes = arg.split(",");
        ZMI root = new ZMI();
        root.setPathName(PathName.ROOT);
        for(String node: nodes){
            ZMI iterator = root;
            PathName name = new PathName(node);
            Iterator<String> nit = name.getComponents().iterator();
            String constructPath = "";
            while(nit.hasNext()){
                String nextComponent = nit.next();
                constructPath = constructPath + "/" + nextComponent;
                for(ZMI son: iterator.getSons()){
                    if(nextComponent.equals(son.getPathName().getSingletonName())){
                        iterator = son; break;
                    }
                }
                if(iterator == root || !nextComponent.equals(iterator.getPathName().getSingletonName())){
                    ZMI next = new ZMI(iterator);
                    iterator.addSon(next);
                    next.setPathName(new PathName(constructPath));
                    iterator = next;
                }

            }

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
                if(arr[0].startsWith("&")){
                    queries.put(arr[0], arr[1]);
                } else {
                    configuration.put(arr[0], arr[1]);
                }
            }
            setMyPath(new PathName(configuration.get("path")));

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

    /**
     * Gossip entry RMI methor
     * @param attrMap - SignedEvent storing ZMI (root)
     * @return
     * @throws RemoteException
     */
    @Override
    public SignedEvent<ZMIContract> gossip(SignedEvent<ZMIContract> attrMap) throws RemoteException {
        try {
            if(!attrMap.validate(QuerySignerModule.getPublicKey()))
                throw new RemoteException("Object validation failed!");
        } catch (Exception e) {
            try {
                throw new RemoteException(
                        "Validation exception! "
                                + e.getMessage()
                                + "\nWith Key: "
                                + Arrays.toString(QuerySignerModule.getPublicKey().getEncoded())
                                + "\nOriginal object trace: "
                                + attrMap.getMessageTrace()
                                + "\nCurrent object: "
                                + attrMap.getMessage().toString()
                                + "\nReceived digest: "+ Arrays.toString(attrMap.getDigest())
                                + "\nActual digest: " + Arrays.toString(SignedEvent.computeHash(attrMap.getMessage())));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        logger.info("Gossiping invoked!");
        ZMI request = attrMap.getMessage().getZmi();
        if(attrMap.getMessage().getTimeInMilis() > getCurrentTime()){
            offset += attrMap.getMessage().getTimeInMilis() - getCurrentTime() + 1000;
        }
        updateZMIStructure(request, Agent.zmi, attrMap.getMessage().getSender(), Agent.getMyPath());
        return querySigner.signEvent(new ZMIContract(zmi.clone(), getCurrentTime(), Agent.getMyPath()));
    }

    @Override
    public SignedEvent<StatusContract> installQuery(SignedEvent<InstallQueryContract> query) throws RemoteException {
        try {
            if(!query.validate(QuerySignerModule.getPublicKey())){
                throw new RemoteException("Object validation failed!" + query.getMessage().toString());
            }
        } catch (Exception e) {
            throw new RemoteException("Validation exception!" + e.getMessage());
        }

        /**
         * install query in all zones upwards from my zone
         */

        ZMI iterator = Agent.zmi.getZoneOrNull(myPath);
        synchronized (Agent.zmi) {
            while (iterator != null) {
                iterator.getAttributes().add("&" + query.getMessage().getQueryName(), new ValueString(query.getMessage().getQuery()));
                iterator.getFreshness().add("&" + query.getMessage().getQueryName(), new ValueDuration(getCurrentTime()));
                iterator = iterator.getFather();
            }
        }
        return querySigner.signEvent(new StatusContract(StatusContract.STATUS.OK));
    }

    @Override
    public SignedEvent<ZMIContract> getZMI(SignedEvent<GetZMIContract> zone) throws RemoteException {
        PathName name = new PathName(zone.getMessage().getZonePath());
        ZMI iterator = Agent.zmi.getZoneOrNull(name);
        if(iterator != null && iterator.getPathName().equals(name)){
            return querySigner.signEvent(new ZMIContract(iterator, Agent.lastZMIupdate, Agent.getMyPath()));
        }

        throw new RemoteException("Could not find ZMI with path "+ name.getName() + " from Agent ZMI: "+ Agent.zmi.getPathName().getName());
    }

    public static void mergeZMI(ZMI request, ZMI target) {
        logger.info("Merging levels: "+ request.getPathName().getName() + " and " + target.getPathName().getName());
        assert(request.getPathName().equals(target.getPathName()));
        String mergedAttrs = "";
        String lastAttr = "";
        Map.Entry<Attribute, Value> valueEntry = null;
        try{
            synchronized (Agent.zmi) {

                Iterator<Map.Entry<Attribute, Value>> iterator = request.getAttributes().iterator();
                while(iterator.hasNext()){
                    valueEntry = iterator.next();
                    mergedAttrs = mergedAttrs + " " + valueEntry.getKey()+":"+valueEntry.getValue().toString();
                    lastAttr = valueEntry.getKey().getName();
                    if(!"name".equals(valueEntry.getKey().getName()) && target.getAttributes().getOrNull(valueEntry.getKey()) == null
                            ||
                            ((ValueBoolean)(target.getFreshness().getOrNull(valueEntry.getKey())
                                    .isLowerThan(request.getFreshness().getOrNull(valueEntry.getKey())))).getValue()){
                        target.getAttributes().addOrChange(valueEntry);
                        target.getFreshness().addOrChange(valueEntry.getKey(), request.getFreshness().get(valueEntry.getKey()));
                    }
                }
            }
        } catch(Exception e){
            logger.severe("Error merging ZMIs: \n"+request.printAttributesToString() + "\n"+target.printAttributesToString()+ "\n -> "+ e.getMessage());
            logger.severe("Merged attrs: " + mergedAttrs);
            logger.severe("Target debug: "+ target.getAttributes().getOrNull(lastAttr) + " valueEntry: "+valueEntry);
            e.printStackTrace();
        }
    }

    /**
     * Updates ZMI according to gossiping
     * @param source new ZMI obtained from contact
     * @param target local ZMI to be updated
     */
    public static void updateZMIStructure(ZMI source, ZMI target, PathName sourcePath, PathName targetPath) throws RemoteException {
        try{
        logger.info("Updating ZMI structure for paths: "+ sourcePath.getName() + " " + Agent.getMyZMI().getPathName().getName());
        synchronized(Agent.zmi) {
            mergeZMI(source, target);
            Iterator<String> targetIterator = targetPath.getComponents().iterator();
            String targetComponent = "";
            for (String component : sourcePath.getComponents()) {
                targetComponent = targetIterator.next();
                logger.info("Merging stage 1: "+ component + ", "+ targetComponent);
                target = target.getSonOrNull(component);
                source = source.getSonOrNull(component);
                mergeZMI(source, target);
                if (!component.equals(targetComponent)) {
                    logger.info("Merging stage 2: " + component + ", " + targetComponent);
                    return;
                }
            }
        }
        } catch(Exception e){
            logger.severe("Updating ZMI Structure failed!");
            e.printStackTrace();
            throw new RemoteException(e.getMessage());
        }
    }
}
