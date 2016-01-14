package pl.edu.mimuw.cloudalbum.agent;

import org.junit.Assert;
import pl.edu.mimuw.cloudalbum.contracts.ZMIContract;
import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudalbum.interfaces.GossipingAgent;
import pl.edu.mimuw.cloudalbum.interfaces.QuerySigner;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.ValueContact;
import pl.edu.mimuw.cloudatlas.model.ValueSet;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.concurrent.*;
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
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int levelDepth = Agent.zmi.getZMIDepth();
        GossipingAgent agent = null;
        int level = 1;
        for(;;){

            try {

                SignedEvent<ZMI> am = querySigner.signEvent(Agent.zmi);
                logger.log(Level.INFO, "Signed attributes map: " + am.toString());

                /**
                 * Select level for gossiping
                 */
                if("roundRobin".equals(Agent.configuration.get("levelSelectionStrategy"))){
                    // round robin
                    level = (level+1)%(levelDepth);
                } else {
                    // random
                    level = new Random().nextInt(levelDepth);
                }
                ZMI gossipLevel = Agent.zmi.getFather().getNLevelsUp(level);
                logger.info("Gossip level: "+ gossipLevel.getPathName().getName());
                logger.info("Gossip level contacts: " + gossipLevel.getAttributes().getOrNull("contacts"));

                try{

                    ValueSet contacts = (ValueSet) gossipLevel.getAttributes().get("contacts");
                    Object vc[] = contacts.getValue().toArray();

                    /**
                     * Random agent selected from contacts list
                     */
                    ValueContact selected = (ValueContact)vc[new Random().nextInt(vc.length)];
                    Registry r = LocateRegistry.getRegistry(selected.getName().getSingletonName(), 1097);
                    // TODO: bind other agent
                    agent = (GossipingAgent) r.lookup("AgentModule");
                    logger.info("Selected Agent for Gossip: "
                             + selected.getName().getName() + ", " + selected.getName().getSingletonName());

                } catch (Exception e){
                    logger.severe("Communication error: " + e.getMessage());
                    e.printStackTrace();
                }
                SignedEvent<ZMIContract> contract = querySigner.signEvent(new ZMIContract(Agent.zmi, Agent.lastZMIupdate));

                Future<SignedEvent<ZMIContract>> future = executor.submit(new GossipCallable(contract, agent));

                try {
                    SignedEvent<ZMIContract> result = future.get(3, TimeUnit.SECONDS);
                    // TODO: merge results with local ZMI

                } catch (TimeoutException e) {
                    future.cancel(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }



            } catch (Exception  e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(Long.parseLong(Agent.configuration.get("agentDelay")));
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "error in sleeping: "+ Agent.configuration.containsKey("agentDelay"));

            }
        }
//        executor.shutdownNow();
    }




    private class GossipCallable implements Callable<SignedEvent<ZMIContract>> {

        private final SignedEvent<ZMIContract> zmi;
        private final GossipingAgent agent;

        public GossipCallable(SignedEvent<ZMIContract> zmi, GossipingAgent other) {
            this.zmi = zmi;
            this.agent = other;
        }

        @Override
        public SignedEvent<ZMIContract> call() throws Exception {
            assert(agent != null);
            return agent.gossip(zmi);
        }
    }


}