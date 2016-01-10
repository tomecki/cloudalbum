package pl.edu.mimuw.cloudalbum.interfaces;


import pl.edu.mimuw.cloudalbum.eda.Event;
import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudalbum.eda.interfaces.Message;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by tomek on 30.12.15.
 */

// TODO: define Query Signer interface
public interface QuerySigner extends Remote {
    public <E extends Serializable> SignedEvent<E> signEvent(E e) throws RemoteException;
}
