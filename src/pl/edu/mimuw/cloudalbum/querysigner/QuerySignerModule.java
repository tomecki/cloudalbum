package pl.edu.mimuw.cloudalbum.querysigner;

import pl.edu.mimuw.cloudalbum.eda.Event;
import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudalbum.interfaces.QuerySigner;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tomek on 02.01.16.
 */
public class QuerySignerModule implements QuerySigner {
    private static final String PRIVATE_KEY_PATH = "private_key.der";
    private static final String PUBLIC_KEY_PATH = "public_key.der";
    private static Logger logger = Logger.getLogger(QuerySignerModule.class.getName());
    public static int signedObjectId = 1;
    public <E extends Serializable> SignedEvent<E> signEvent(E o) throws RemoteException {
        try {
            logger.log(Level.INFO, "Signing message: " + o.toString() + "\nWith key: " + Arrays.toString(QuerySignerModule.getPublicKey().getEncoded()));
            return new SignedEvent(o, QuerySignerModule.getPrivateKey());
        } catch(Exception e){
            throw new RemoteException("Error signing the message: " + e.getMessage());
        }
    }
    private static PublicKey publicKey;
    private static PrivateKey privateKey;
    public static PublicKey getPublicKey() throws Exception {
        if(publicKey == null){
            File f = new File(PUBLIC_KEY_PATH);
            FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);
            byte[] keyBytes = new byte[(int)f.length()];
            dis.readFully(keyBytes);
            dis.close();
            X509EncodedKeySpec spec =
                    new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            publicKey = kf.generatePublic(spec);
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey() throws Exception{
        if(privateKey == null){
            File f = new File(PRIVATE_KEY_PATH);
            FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);
            byte[] keyBytes = new byte[(int)f.length()];
            dis.readFully(keyBytes);
            dis.close();

            PKCS8EncodedKeySpec spec =
                    new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            privateKey = kf.generatePrivate(spec);
        }
        return privateKey;
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
            logger.log(Level.INFO, "QuerySignerModule object exported");
            Registry registry = LocateRegistry.getRegistry("localhost", Integer.parseInt(args[0]));
            logger.log(Level.INFO, "Registry located at" + Integer.parseInt(args[0]));
            registry.rebind(name, stub);
            logger.log(Level.INFO, "QuerySignerModule bound");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }
}
