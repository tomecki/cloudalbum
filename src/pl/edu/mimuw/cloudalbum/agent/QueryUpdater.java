package pl.edu.mimuw.cloudalbum.agent;

import pl.edu.mimuw.cloudalbum.interfaces.QuerySigner;
import pl.edu.mimuw.cloudatlas.interpreter.Main;
import pl.edu.mimuw.cloudatlas.model.Attribute;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueString;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by tomek on 13.01.16.
 */
public class QueryUpdater implements Runnable {
    private QuerySigner querySigner;
    private Logger logger = Logger.getLogger(QueryUpdater.class.getName());

    public void run() {
        ZMI root = Agent.zmi.getRoot();
        for(;;) {
            synchronized (Agent.zmi) {
                logger.info("Query updater invoked on "+ Agent.getMyZMI().getPathName().getName());
                try {
                    Iterator<Map.Entry<Attribute, Value>> iterator = root.getAttributes().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Attribute, Value> entry = iterator.next();
                        if (Attribute.isQuery(entry.getKey())) {
                            logger.info("Executing query: " + entry.getValue().toString() + " on ZMI: " + root.toString());
                            try {
                                Main.executeQueries(root, ((ValueString) (entry.getValue())).getValue());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e){
                    logger.severe("Exception while updating query results: " + e.getMessage());
                    e.printStackTrace();
                }
                logger.info("Query updater finished:\n" + Agent.zmi.printAttributesToString());
            }
            try {
                Thread.sleep(Long.parseLong(Agent.configuration.get("agentDelay")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
