package pl.edu.mimuw.cloudalbum.tests;


import org.junit.Assert;
import org.junit.Test;
import pl.edu.mimuw.cloudalbum.interfaces.Fetcher;
import pl.edu.mimuw.cloudalbum.fetcher.FetcherModule;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by tomek on 12.12.15.
 */
public class FetcherTest {
    @Test
    public void testFetcher() throws RemoteException {
        Fetcher f = new FetcherModule();
        AttributesMap am = f.getLocalStats();
        Assert.assertTrue(am != null);
    }

    @Test
    public void testRemoteFetcher() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost");
        Fetcher stub = (Fetcher) registry.lookup("FetcherModule");
        AttributesMap am = stub.getLocalStats();
        Assert.assertTrue(am != null);
    }
}
