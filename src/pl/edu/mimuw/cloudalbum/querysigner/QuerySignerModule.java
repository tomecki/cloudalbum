package pl.edu.mimuw.cloudalbum.querysigner;

import pl.edu.mimuw.cloudalbum.eda.Event;
import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudalbum.interfaces.QuerySigner;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tomek on 02.01.16.
 */
public class QuerySignerModule implements QuerySigner {
    private static Logger logger = Logger.getLogger(QuerySignerModule.class.getName());
    public SignedEvent signEvent(Event m) {
        return SignedEvent.sign(m);
    }
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "QuerySignerModule";
            QuerySignerModule engine = new QuerySignerModule();
            QuerySigner stub =
                    (QuerySigner) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            logger.log(Level.INFO, "QuerySignerModule bound");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
