package pl.edu.mimuw.cloudalbum.tests;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.mimuw.cloudalbum.agent.Agent;
import pl.edu.mimuw.cloudatlas.interpreter.Main;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.UnknownHostException;
import java.security.*;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tomek on 07.01.16.
 */
public class AgentTest {
    private static Logger logger = Logger.getLogger(AgentTest.class.getName());
    @Test
    public void testZmiHierarchy(){
        String path = "/uw/mimuw/violet/violet01,/uw/mimuw/violet/violet04,/uw/mimuw/violet/violet06,/uw/mimuw/blue/blue10,/uw/mimuw/blue/blue13,/uw/mimuw/blue/blue12";
        ZMI root = Agent.createZMIHierarchy(path, "/uw/mimuw/violet/violet06");
        logger.log(Level.INFO, root.toString());
        logger.log(Level.INFO, root.getFather().toString());
    }
    @Test
    public void testZmiPredefined() throws ParseException, UnknownHostException {
        ZMI root = Main.createTestHierarchy();
        logger.info(root.toString());
    }

    @Test
    public void readConfiguration(){
        Agent.readConfiguration("conf/blue10");
        String path = "/uw/mimuw/violet/violet01,/uw/mimuw/violet/violet04,/uw/mimuw/violet/violet06,/uw/mimuw/blue/blue10,/uw/mimuw/blue/blue13,/uw/mimuw/blue/blue12";
        ZMI root = Agent.createZMIHierarchy(path, "/uw/mimuw/blue/blue10");
        Agent.fillContacts(root, Agent.configuration);
        Assert.assertTrue(1==1);
    }
    private final static String ENCRYPTION_ALGORITHM = "RSA";
    private final static int NUM_KEY_BITS = 1024;
    private final static byte[] SAMPLE_BYTES =
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

}
