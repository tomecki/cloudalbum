package pl.edu.mimuw.cloudalbum.eda;

import pl.edu.mimuw.cloudalbum.eda.interfaces.Message;
import pl.edu.mimuw.cloudalbum.querysigner.QuerySignerModule;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
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
    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        QuerySignerModule.signedObjectId = QuerySignerModule.signedObjectId + 1;
        this.objectId = objectId;
    }

    private int objectId;

    private String messageTrace;

    public String getMessageTrace() {
        return messageTrace;
    }

    public void setMessageTrace(String messageTrace) {
        this.messageTrace = messageTrace;
    }

    public byte[] getDigest() {
        return digest;
    }

    public void setDigest(byte[] digest) {
        this.digest = digest;
    }

    private byte[] digest;

    public SignedEvent(E e, PrivateKey privateKey) {
        super();
        setMessage(e);
        try {
            setHash(privateKey);
        } catch (Exception e1) {
            logger.log (Level.SEVERE, e1.getMessage());
        }
    }

    private E message;

    private byte[] hash;
    private void setHash(PrivateKey privateKey) throws Exception {
        this.hash = sign(computeHash(this.message), privateKey);
        this.digest = computeHash(this.message);
        this.messageTrace = this.message.toString();
    }

    private byte[] sign(byte[] bytes, PrivateKey privateKey) throws Exception {
        this.setObjectId(QuerySignerModule.signedObjectId);
        Cipher signCipher = signCipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        signCipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return signCipher.doFinal(bytes);

    }

    public boolean validate(PublicKey publicKey) throws Exception {
//        logger.info("Validating against " + this.getMessage().toString() + "\nWith key: "+ Arrays.toString(QuerySignerModule.getPublicKey().getEncoded()));
        return validate(this.hash, computeHash(this.getMessage()), publicKey);
    }

    public boolean validate(E e, PublicKey publicKey) throws Exception {
        return validate(this.hash, computeHash(e), publicKey);
    }

    private boolean validate(byte[] signedHash, byte[] originalHash, PublicKey publicKey) throws Exception {
        Cipher verifyCipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        verifyCipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedBytes = verifyCipher.doFinal(signedHash);
//        logger.info("validation: "
//                + Arrays.toString(decryptedBytes)
//                + "\n"
//                + Arrays.toString(originalHash));

        return Arrays.equals(decryptedBytes, originalHash);
    }

    public static<E> byte[] computeHash(E message) throws IOException {
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
