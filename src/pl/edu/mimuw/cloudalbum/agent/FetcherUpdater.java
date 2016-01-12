package pl.edu.mimuw.cloudalbum.agent;

import pl.edu.mimuw.cloudalbum.interfaces.Fetcher;
import pl.edu.mimuw.cloudatlas.model.*;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tomek on 09.01.16.
 */
public class FetcherUpdater implements Runnable {
    private static Logger logger = Logger.getLogger(FetcherUpdater.class.getName());
    private ZMI zmi;
    private Fetcher fetcher;
    private AttributesMap currentState;

    public FetcherUpdater(Fetcher fetcher, ZMI zmi) {
        this.fetcher = fetcher; this.zmi = zmi;
        logger.log(Level.INFO, "FetcherUpdater initiated with ZMI: "+ zmi.toString());
    }

    @Override
    public void run() {
        long delay = 2000;
        try {
            delay = Long.parseLong(Agent.configuration.get("fetcherFrequency"));
        } catch(Exception e){
            logger.log(Level.WARNING, "Configuration error: no fetcherFrequency set up");
        }

        logger.log(Level.INFO, "FetcherUpdater thread started");

        for(;;){
            logger.log(Level.INFO, "Fetching local stats");
            synchronized(this){
                currentState = FetcherUpdater.getLocalStats(fetcher);
                logger.log(Level.INFO, "Local stats fetched, updating");
                Agent.zmi.getAttributes().addOrChange(currentState);
                Iterator<Map.Entry<Attribute, Value>> it = currentState.iterator();
                while(it.hasNext()){
                    Map.Entry<Attribute, Value> v = it.next();
                    Agent.zmi.getFreshness().addOrChange(v.getKey(), new ValueDuration(Agent.calendar.getTimeInMillis()));
                }
            }
            logger.log(Level.INFO, "Fetching local stats finished");
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private static AttributesMap getLocalStats(Fetcher fetcher){
        AttributesMap am = null;
        try {
            logger.log(Level.INFO, "Fetching local stats " + fetcher.getClass().toString());
            am = fetcher.getLocalStats();
            logger.log(Level.INFO, "Local stats fetched: "+ am.toString());

            Iterator<Map.Entry<Attribute, Value>> it;
            it = am.iterator();
            if(am!=null) {
                while (it.hasNext()) {
                    Map.Entry<Attribute, Value> e = it.next();
                    System.out.println(e.getKey().getName() + " " + e.getValue().toString());
                }
            }
            return am;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;

    }
}
