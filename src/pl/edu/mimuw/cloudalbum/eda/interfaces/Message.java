package pl.edu.mimuw.cloudalbum.eda.interfaces;

/**
 * Created by tomek on 30.12.15.
 */
public interface Message {
    public Class<? extends Message> getType();
}
