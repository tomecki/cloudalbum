package pl.edu.mimuw.cloudalbum.eda.interfaces;

/**
 * Created by tomek on 30.12.15.
 */
public interface Router<M extends Message> {
    public void registerChannel(Class<? extends M> contentType, Channel<? extends M> channel);
    public abstract void dispatch(M content);
}
