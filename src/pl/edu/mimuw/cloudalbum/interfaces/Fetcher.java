package pl.edu.mimuw.cloudalbum.interfaces;

import pl.edu.mimuw.cloudatlas.model.AttributesMap;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by tomek on 08.12.15.
 */
public interface Fetcher extends Remote {
    AttributesMap getLocalStats() throws RemoteException;
}
