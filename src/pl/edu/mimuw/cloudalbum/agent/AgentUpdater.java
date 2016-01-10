package pl.edu.mimuw.cloudalbum.agent;

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
        this.querySigner = querySigner;
    }

    @Override
    public void run() {
        for(;;){
            try {
                SignedEvent<AttributesMap> am = querySigner.signEvent(Agent.zmi.getAttributes());
                logger.log(Level.INFO, am.toString());
                Thread.sleep(Long.parseLong(Agent.configuration.get("agentDelay")));
            } catch (Exception  e) {
                e.printStackTrace();
            }
        }
    }
}