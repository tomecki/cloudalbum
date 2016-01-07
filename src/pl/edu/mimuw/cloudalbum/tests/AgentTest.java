package pl.edu.mimuw.cloudalbum.tests;

import org.junit.Test;
import pl.edu.mimuw.cloudalbum.agent.Agent;
import pl.edu.mimuw.cloudatlas.model.ZMI;

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
}
