package pl.edu.mimuw.cloudalbum.interfaces;

import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by tomek on 09.01.16.
 */
public interface GossipingAgent extends Remote{
    public SignedEvent<ZMI> gossip(SignedEvent<ZMI> attrMap) throws RemoteException;
}
