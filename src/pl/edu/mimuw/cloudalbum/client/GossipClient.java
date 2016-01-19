package pl.edu.mimuw.cloudalbum.client;

import pl.edu.mimuw.cloudalbum.agent.Agent;
import pl.edu.mimuw.cloudalbum.contracts.GetZMIContract;
import pl.edu.mimuw.cloudalbum.contracts.InstallQueryContract;
import pl.edu.mimuw.cloudalbum.contracts.StatusContract;
import pl.edu.mimuw.cloudalbum.contracts.ZMIContract;
import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudalbum.interfaces.Client;
import pl.edu.mimuw.cloudalbum.interfaces.GossipingAgent;
import pl.edu.mimuw.cloudalbum.interfaces.QuerySigner;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by tomek on 18.01.16.
 */
public class GossipClient implements Client {
    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {
        Registry qsRegistry = LocateRegistry.getRegistry(args[0], 1097);
        QuerySigner querySigner = (QuerySigner) qsRegistry.lookup("QuerySignerModule");

        String host = args[1];
        String command  = args[2];
        if("show".equals(command)){
            System.out.println(getZMI(host, args[3], querySigner).toString());
        } else if("install".equals(command)){
            for(String line: Files.readAllLines(Paths.get(args[3]), Charset.defaultCharset())){
                String[] split = line.split(":");
                installQuery(host, split[0], split[1], querySigner);
            }

        } else  if("monitor".equals(command)){
            monitor(host, args[3], args[4], querySigner);
        }
    }

    public static ZMI getZMI(String host, String path, QuerySigner querySigner) throws RemoteException, NotBoundException {
        SignedEvent<GetZMIContract> getZMIContractSignedEvent = querySigner.signEvent(new GetZMIContract(path));
        Registry registry = LocateRegistry.getRegistry(host, 1097);
        GossipingAgent agent = (GossipingAgent) registry.lookup("AgentModule");
        SignedEvent<ZMIContract> result = agent.getZMI(getZMIContractSignedEvent);
        return result.getMessage().getZmi();
    }

    public static void monitor(String host, String path, String attr, QuerySigner querySigner) throws InterruptedException, RemoteException, NotBoundException {
        for(;;){
            ZMI requested = getZMI(host, path, querySigner);
            System.out.println(requested.getAttributes().getOrNull(attr) + "\t" + requested.getFreshness().getOrNull(attr));
            Thread.sleep(1000);
        }
    }
    public static void installQuery(String host, String queryName, String query, QuerySigner querySigner) throws RemoteException, NotBoundException {
        SignedEvent<InstallQueryContract> installQueryContractSignedEvent = querySigner.signEvent(new InstallQueryContract(queryName, query));
        Registry registry = LocateRegistry.getRegistry(host, 1097);
        GossipingAgent agent = (GossipingAgent) registry.lookup("AgentModule");
        SignedEvent<StatusContract> result = agent.installQuery(installQueryContractSignedEvent);
        System.out.println(result.getMessage().toString());
    }
}
