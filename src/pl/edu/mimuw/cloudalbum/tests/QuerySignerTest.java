package pl.edu.mimuw.cloudalbum.tests;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.mimuw.cloudalbum.agent.Agent;
import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.io.IOException;

/**
 * Created by tomek on 10.01.16.
 */


public class QuerySignerTest {

    @Test
    public void SignTest() throws Exception {
        String path = "/a/b/c/d/e/f/g";
        ZMI root = Agent.createZMIHierarchy(path, path);
        String path2 = "/a/b/c/d/e/f/h";
        ZMI root2 = Agent.createZMIHierarchy(path2, path);
        SignedEvent<AttributesMap> am = new SignedEvent<>(root.getAttributes());
        SignedEvent<AttributesMap> am2 = new SignedEvent<>(root2.getAttributes());
        for(byte b: am.computeHash(am.getMessage())){
            System.err.print(b);
        } System.err.println();
        for(byte b: am.getHash()){
            System.err.print(b);
        } System.err.println();
        Assert.assertTrue(am.validate(root.getAttributes()));
        Assert.assertFalse(am.validate(root2.getAttributes()));

    }
}
