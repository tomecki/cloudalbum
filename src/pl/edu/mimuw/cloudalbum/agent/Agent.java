package pl.edu.mimuw.cloudalbum.agent;

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
    public static final Calendar calendar = Calendar.getInstance();

    public static QuerySigner querySigner;

    public static void main(String args[]){
        readConfiguration(args.length==0?"settings.conf" : args[1]);
        zmi = createZMIHierarchy(configuration.get("allNodes"), configuration.get("path"));
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



        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void fillContacts(ZMI root, Map<String, String> configuration) {
        try{
            logger.info("configuration: "+ configuration.toString());
            String[] querySigner = configuration.get("querySigner").split(",");
            Agent.getMyZMI().getAttributes().addOrChange("querySigner", new ValueContact(new PathName(querySigner[0]), InetAddress.getByName(querySigner[1])));
            Agent.getMyZMI().getFreshness().addOrChange("querySigner", new ValueDuration(Agent.calendar.getTimeInMillis()));
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
                configuration.put(arr[0], arr[1]);
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

        ZMI iterator = zmi;
        ZMI requestIterator = request;
        for(String component: myPath.getComponents()){
            updateLocalZMI(requestIterator, iterator);
            for(ZMI son: requestIterator.getSons()) {
                if(son.getPathName().getSingletonName().equals(component)){
                    requestIterator = son; break;
                }
            }
            for(ZMI son: iterator.getSons()) {
                if(son.getPathName().getSingletonName().equals(component)){
                    iterator = son; break;
                }
            }
            if(!requestIterator.equals(component) || !iterator.equals(component))
                break;

        }


        return querySigner.signEvent(new ZMIContract(zmi.clone(), Agent.calendar.getTimeInMillis()));
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
        while(iterator != null){
            iterator.getAttributes().add(query.getMessage().getQueryName(), new ValueString(query.getMessage().getQuery()));
            iterator.getFreshness().add(query.getMessage().getQueryName(), new ValueDuration(Agent.calendar.getTimeInMillis()));
            iterator = iterator.getFather();
        }

        return querySigner.signEvent(new StatusContract(StatusContract.STATUS.OK));
    }

    @Override
    public SignedEvent<ZMIContract> getZMI(SignedEvent<String> zone) throws RemoteException {
        ZMI iterator = Agent.zmi;
        PathName name = new PathName(zone.getMessage());
        while(iterator!=null){
            if(iterator.getPathName().equals(name)){
                return querySigner.signEvent(new ZMIContract(iterator, Agent.lastZMIupdate));
            }
        }
        throw new RemoteException("Could not find ZMI with path "+ name.getName() + " from Agent ZMI: "+ Agent.zmi.getPathName().getName());
    }

    private void updateLocalZMI(ZMI request, ZMI target) {
        assert(request.getPathName().equals(target.getPathName()));
        try{
            synchronized (this) {
                Iterator<Map.Entry<Attribute, Value>> iterator = request.getAttributes().iterator();
                while(iterator.hasNext()){
                    Map.Entry<Attribute, Value> valueEntry = iterator.next();
                    if(target.getAttributes().getOrNull(valueEntry.getKey()) == null
                            ||
                            ((ValueBoolean)(target.getFreshness().getOrNull(valueEntry.getKey())
                                    .isLowerThan(request.getFreshness().getOrNull(valueEntry.getKey())))).getValue()){
                        target.getAttributes().addOrChange(valueEntry);
                        target.getFreshness().addOrChange(valueEntry.getKey(), request.getFreshness().get(valueEntry.getKey()));
                    }
                }
            }
        } catch(Exception e){
            logger.severe("Error merging ZMIs: \n"+request.toString() + "\n"+target.toString()+ "\n -> "+ e.getMessage());
            e.printStackTrace();
        }
    }
}
