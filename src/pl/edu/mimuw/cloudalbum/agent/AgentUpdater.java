package pl.edu.mimuw.cloudalbum.agent;

import org.junit.Assert;
import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudalbum.interfaces.QuerySigner;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tomek on 09.01.16.
 */
public class AgentUpdater implements Runnable {
    private Logger logger = Logger.getLogger(AgentUpdater.class.getName());
    private final QuerySigner querySigner;

    public AgentUpdater(QuerySigner querySigner) {
        logger.log(Level.INFO, "Creating AgentUpdater with querySigner: "+ querySigner.toString());
        this.querySigner = querySigner;
    }

    @Override
    public void run() {
//        for(;;){
        logger.log(Level.INFO, "Attributes map: "+ Agent.zmi.getAttributes());
            try {

                logger.log(Level.INFO, "Attributes map: "+ Agent.zmi.getAttributes());
                SignedEvent<AttributesMap> am = querySigner.signEvent(Agent.zmi.getAttributes());
                logger.log(Level.INFO, "Signed attributes map: " + am.toString());

            } catch (Exception  e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(Long.parseLong(Agent.configuration.get("agentDelay")));
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "error in sleeping: "+ Agent.configuration.containsKey("agentDelay"));

            }
//        }
    }
}