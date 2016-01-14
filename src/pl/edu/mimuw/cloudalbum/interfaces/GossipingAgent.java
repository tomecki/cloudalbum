package pl.edu.mimuw.cloudalbum.interfaces;

import pl.edu.mimuw.cloudalbum.contracts.InstallQueryContract;
import pl.edu.mimuw.cloudalbum.contracts.StatusContract;
import pl.edu.mimuw.cloudalbum.contracts.ZMIContract;
import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by tomek on 09.01.16.
 */
public interface GossipingAgent extends Remote{
    public SignedEvent<ZMIContract> gossip(SignedEvent<ZMIContract> attrMap) throws RemoteException;
    public SignedEvent<StatusContract> installQuery(SignedEvent<InstallQueryContract> query) throws RemoteException;
    public SignedEvent<ZMIContract> getZMI(SignedEvent<String> zone) throws RemoteException;
    // TODO: uninstalling queries
}
