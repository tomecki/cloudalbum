package pl.edu.mimuw.cloudalbum.tests;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.mimuw.cloudalbum.agent.Agent;
import pl.edu.mimuw.cloudalbum.contracts.InstallQueryContract;
import pl.edu.mimuw.cloudalbum.contracts.StatusContract;
import pl.edu.mimuw.cloudalbum.contracts.ZMIContract;
import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudalbum.querysigner.QuerySignerModule;
import pl.edu.mimuw.cloudatlas.interpreter.Main;
import pl.edu.mimuw.cloudatlas.model.ValueDuration;
import pl.edu.mimuw.cloudatlas.model.ValueString;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
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

    @Test
    public void gossipTest() throws RemoteException {
        String path = "/uw/mimuw/violet/violet01,/uw/mimuw/violet/violet04,/uw/mimuw/violet/violet06,/uw/mimuw/blue/blue10,/uw/mimuw/blue/blue13,/uw/mimuw/blue/blue12";
        ZMI root = Agent.createZMIHierarchy(path, "/uw/mimuw/blue/blue10");
        ZMI request = Agent.createZMIHierarchy(path, "/uw/mimuw/blue/blue13");

        request = request.getFather().getFather();
        Agent.zmi = root;
        ZMI iterator = root.getFather().getFather();
        Agent.querySigner = new QuerySignerModule();

        iterator.getAttributes().addOrChange("a1", new ValueString("b"));
        iterator.getAttributes().addOrChange("a2", new ValueString("c"));
        iterator.getAttributes().addOrChange("a3", new ValueString("d"));

        iterator.getFreshness().addOrChange("a1", new ValueDuration(Agent.calendar.getTimeInMillis()));
        iterator.getFreshness().addOrChange("a2", new ValueDuration(Agent.calendar.getTimeInMillis()-1000));
        iterator.getFreshness().addOrChange("a3", new ValueDuration(Agent.calendar.getTimeInMillis()));

        request.getAttributes().addOrChange("a1", new ValueString("x"));
        request.getAttributes().addOrChange("a2", new ValueString("y"));
        request.getAttributes().addOrChange("a3", new ValueString("z"));

        request.getFreshness().addOrChange("a1", new ValueDuration(Agent.calendar.getTimeInMillis()-1000));
        request.getFreshness().addOrChange("a2", new ValueDuration(Agent.calendar.getTimeInMillis()));
        request.getFreshness().addOrChange("a3", new ValueDuration(Agent.calendar.getTimeInMillis()-1000));

        final SignedEvent<ZMIContract> gossip = new Agent().gossip(Agent.querySigner.signEvent(new ZMIContract(request, Agent.calendar.getTimeInMillis())));
        Assert.assertTrue(1 == 1);
    }

    @Test
    public void installQueryTest() throws RemoteException {
        Agent.querySigner = new QuerySignerModule();

        String path = "/uw/mimuw/violet/violet01,/uw/mimuw/violet/violet04,/uw/mimuw/violet/violet06,/uw/mimuw/blue/blue10,/uw/mimuw/blue/blue13,/uw/mimuw/blue/blue12";
        Agent.zmi = Agent.createZMIHierarchy(path, "/uw/mimuw/blue/blue10");
        SignedEvent<StatusContract> result =  new Agent().installQuery(Agent.querySigner.signEvent(new InstallQueryContract("&q1", "SELECT 1+1 AS one")));
        Assert.assertTrue(result.getMessage().getStatus() == StatusContract.STATUS.OK);
    }
}
