package pl.edu.mimuw.cloudalbum.interfaces;


import pl.edu.mimuw.cloudalbum.eda.Event;
import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudalbum.eda.interfaces.Message;

import java.rmi.Remote;

/**
 * Created by tomek on 30.12.15.
 */

// TODO: define Query Signer interface
public interface QuerySigner extends Remote {
    public SignedEvent signEvent(Event m);
}
