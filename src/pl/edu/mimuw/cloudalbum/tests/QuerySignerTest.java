package pl.edu.mimuw.cloudalbum.tests;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.mimuw.cloudalbum.agent.Agent;
import pl.edu.mimuw.cloudalbum.contracts.ZMIContract;
import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudalbum.interfaces.QuerySigner;
import pl.edu.mimuw.cloudalbum.querysigner.QuerySignerModule;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by tomek on 10.01.16.
 */


public class QuerySignerTest {

    @Test
    public void SignTest() throws Exception {
        String path = "/a/b/c/d/e/f/g";
        ZMI root = Agent.createZMIHierarchy(path, path);

        String path2 = "/a/b/c/d/e/f/h";
        ZMI root2 = Agent.createZMIHierarchy(path2, path2);
        SignedEvent<ZMI> am = new SignedEvent<>(root, QuerySignerModule.getPrivateKey());
        SignedEvent<ZMI> am2 = new SignedEvent<>(root2, QuerySignerModule.getPrivateKey());
        for(byte b: am.computeHash(am.getMessage())){
            System.err.print(b);
        } System.err.println();
        for(byte b: am.getHash()){
            System.err.print(b);
        } System.err.println();

        ZMI myZone = root.getZoneOrNull(new PathName("/a/b/c"));

        Assert.assertTrue(am.validate(QuerySignerModule.getPublicKey()));
        Assert.assertTrue(am2.validate(QuerySignerModule.getPublicKey()));
        am2.setMessage(root);
        Assert.assertFalse(am2.validate(QuerySignerModule.getPublicKey()));




    }

    @Test
    public void SignTest2() throws Exception {
        String path = "/uw/mimuw/violet/violet01,/uw/mimuw/violet/violet04,/uw/mimuw/violet/violet06,/uw/mimuw/blue/blue10,/uw/mimuw/blue/blue13,/uw/mimuw/blue/blue12";
        ZMI root = Agent.createZMIHierarchy(path, "/uw/mimuw/blue/blue10");
        ZMI root2 = Agent.createZMIHierarchy(path, "/uw/mimuw/blue/blue10");

        ZMIContract z = new ZMIContract(root, Calendar.getInstance().getTimeInMillis(), new PathName("/uw/mimuw/blue/blue10"));
//        Thread.sleep(1000);
        ZMIContract z2 = new ZMIContract(root2, Calendar.getInstance().getTimeInMillis(), new PathName("/uw/mimuw/blue/blue10"));
        SignedEvent<ZMIContract> zc = new SignedEvent<>(z, QuerySignerModule.getPrivateKey());
        Assert.assertTrue(zc.validate(z2, QuerySignerModule.getPublicKey()));

    }
}
