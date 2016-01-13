package pl.edu.mimuw.cloudalbum.agent;

import pl.edu.mimuw.cloudalbum.interfaces.QuerySigner;
import pl.edu.mimuw.cloudatlas.model.ZMI;

/**
 * Created by tomek on 13.01.16.
 */
public class QueryUpdater implements Runnable {
    private QuerySigner querySigner;

    @Override
    public void run() {
        ZMI iterator = Agent.zmi;
        while(iterator != null){

        }
    }
}
