package pl.edu.mimuw.cloudalbum.eda;

import pl.edu.mimuw.cloudalbum.eda.interfaces.Message;

/**
 * Created by tomek on 30.12.15.
 */
public class Event implements Message {
    @Override
    public Class<? extends Message> getType() {
        return getClass();
    }
}
