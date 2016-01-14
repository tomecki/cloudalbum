package pl.edu.mimuw.cloudalbum.eda;

import pl.edu.mimuw.cloudalbum.eda.interfaces.Message;
import pl.edu.mimuw.cloudalbum.querysigner.QuerySignerModule;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tomek on 30.12.15.
 */
public class SignedEvent<E extends Serializable> implements Serializable {
    private static final java.lang.String DIGEST_ALGORITHM = "SHA-256";
    private static final java.lang.String ENCRYPTION_ALGORITHM = "RSA";
    private static Logger logger = Logger.getLogger(SignedEvent.class.getName());
    public SignedEvent(E e) {
        super();
        setMessage(e);
        try {
            setHash();
        } catch (Exception e1) {
            logger.log (Level.SEVERE, e1.getMessage());
        }
    }

    private E message;

    private byte[] hash;
    private void setHash() throws Exception {
        this.hash = sign(computeHash(this.message));
    }

    private byte[] sign(byte[] bytes) throws Exception {
        Cipher signCipher = signCipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        signCipher.init(Cipher.ENCRYPT_MODE, QuerySignerModule.getPrivateKey());
        return signCipher.doFinal(bytes);

    }

    public boolean validate() throws Exception {
        return validate(this.hash, computeHash(this.getMessage()));
    }

    public boolean validate(E e) throws Exception {
        return validate(this.hash, computeHash(e));
    }

    private boolean validate(byte[] signedHash, byte[] originalHash) throws Exception {
        Cipher verifyCipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        verifyCipher.init(Cipher.DECRYPT_MODE, QuerySignerModule.getPublicKey());
        byte[] decryptedBytes = verifyCipher.doFinal(signedHash);
        logger.info("validation: "
                + Arrays.toString(decryptedBytes)
                + "\n"
                + Arrays.toString(originalHash));
        
        return Arrays.equals(decryptedBytes, originalHash);
    }

    public byte[] computeHash(E message) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(message);
        byte[] yourBytes = bos.toByteArray();
        MessageDigest digestGenerator =
                null;
        try {
            digestGenerator = MessageDigest.getInstance(DIGEST_ALGORITHM);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        byte[] digest = digestGenerator.digest(yourBytes);
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException ex) {
            // ignore close exception
        }
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return digest;

    }

    public E getMessage() {
        return message;
    }

    public void setMessage(E message) {
        this.message = message;
    }

    public byte[] getHash() {
        return hash;
    }
}
