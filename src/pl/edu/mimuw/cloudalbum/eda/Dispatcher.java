package pl.edu.mimuw.cloudalbum.eda;

import pl.edu.mimuw.cloudalbum.eda.interfaces.Channel;
import pl.edu.mimuw.cloudalbum.eda.interfaces.Router;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomek on 30.12.15.
 */
public class Dispatcher implements Router<Event> {
    private Map<Class<? extends Event>, Handler> handlers;

    public Dispatcher() {
        handlers = new HashMap<>();
    }

    @Override
    public void registerChannel(Class<? extends Event> contentType, Channel<? extends Event> channel) {
        handlers.put(contentType, (Handler)channel);
    }

    @Override
    public void dispatch(Event content){
        handlers.get(content.getClass()).dispatch(content);
    }
}
