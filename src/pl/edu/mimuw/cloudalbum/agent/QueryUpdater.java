package pl.edu.mimuw.cloudalbum.agent;

import pl.edu.mimuw.cloudalbum.interfaces.QuerySigner;
import pl.edu.mimuw.cloudatlas.interpreter.Main;
import pl.edu.mimuw.cloudatlas.model.Attribute;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueString;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by tomek on 13.01.16.
 */
public class QueryUpdater implements Runnable {
    private QuerySigner querySigner;

    @Override
    public void run() {
        ZMI root = Agent.zmi.getRoot();
        for(;;) {
            Iterator<Map.Entry<Attribute, Value>> iterator = Agent.zmi.getAttributes().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Attribute, Value> entry = iterator.next();
                if (Attribute.isQuery(entry.getKey())) {
                    try {
                        Main.executeQueries(root, ((ValueString) (entry.getValue())).getValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(Long.parseLong(Agent.configuration.get("agentDelay")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
