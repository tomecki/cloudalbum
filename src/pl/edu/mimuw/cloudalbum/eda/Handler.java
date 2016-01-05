package pl.edu.mimuw.cloudalbum.eda;

import pl.edu.mimuw.cloudalbum.eda.interfaces.Channel;

/**
 * Created by tomek on 30.12.15.
 */
public class Handler implements Channel<Event> {
    @Override
    public void dispatch(Event message) {
        System.out.println(message.getClass());
    }
}
