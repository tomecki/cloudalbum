package pl.edu.mimuw.cloudalbum.interfaces;

import pl.edu.mimuw.cloudalbum.eda.SignedEvent;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.io.Serializable;

/**
 * Created by tomek on 12.01.16.
 */
public interface Client {
    public SignedEvent<ZMI> getAgentZMI();
}
