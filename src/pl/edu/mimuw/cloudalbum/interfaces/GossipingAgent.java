package pl.edu.mimuw.cloudalbum.interfaces;

import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudatlas.model.AttributesMap;

import java.rmi.Remote;

/**
 * Created by tomek on 09.01.16.
 */
public interface GossipingAgent extends Remote{
    public SignedEvent<AttributesMap> gossip(SignedEvent<AttributesMap> attrMap);
}
