package pl.edu.mimuw.cloudalbum.tests;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.mimuw.cloudalbum.agent.Agent;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tomek on 07.01.16.
 */
public class AgentTest {
    private static Logger logger = Logger.getLogger(AgentTest.class.getName());
    @Test
    public void testZmiHierarchy(){
        String path = "/a/b/c/d/e/f/g";
        ZMI root = Agent.createZMIHierarchy(path);
        logger.log(Level.INFO, root.toString());
        logger.log(Level.INFO, root.getFather().toString());
    }
    @Test
    public void readConfiguration(){
        Agent.readConfiguration("conf/yellow10");
        ZMI root = Agent.createZMIHierarchy(Agent.configuration.get("path"));
        Agent.fillContacts(root, Agent.configuration);
        Assert.assertTrue(1==1);
    }
    private final static String ENCRYPTION_ALGORITHM = "RSA";
    private final static int NUM_KEY_BITS = 1024;
    private final static byte[] SAMPLE_BYTES =
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    @Test
    public void generateKeys(){
        try {
            // Generate a key pair.
            KeyPairGenerator keyGenerator =
                    KeyPairGenerator.getInstance(ENCRYPTION_ALGORITHM);
            keyGenerator.initialize(NUM_KEY_BITS);
            KeyPair keyPair = keyGenerator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
//            System.out.println(
//                    "Original bytes: " +
//                            ByteArrayUtils.byteArrayToString(
//                                    SAMPLE_BYTES, 0, SAMPLE_BYTES.length));
            // Encrypt bytes (rather sign them, as we use the private key).
            Cipher signCipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            signCipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] encryptedBytes = signCipher.doFinal(SAMPLE_BYTES);
//            System.out.println(
//                    "Bytes encrypted with " + ENCRYPTION_ALGORITHM +
//                            ": " + ByteArrayUtils.byteArrayToString(
//                            encryptedBytes, 0, encryptedBytes.length));
            // Decrypt bytes (rather verify the signature).
            Cipher verifyCipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            verifyCipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] decryptedBytes = verifyCipher.doFinal(encryptedBytes);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            String path = "/a/b/c/d/e/f/g";
            ZMI root = Agent.createZMIHierarchy(path);






//            System.out.println(
//                    "Bytes decrypted with " + ENCRYPTION_ALGORITHM +
//                            ": " + ByteArrayUtils.byteArrayToString(
//                            decryptedBytes, 0, decryptedBytes.length));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }
}
