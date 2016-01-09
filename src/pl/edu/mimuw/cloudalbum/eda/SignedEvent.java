package pl.edu.mimuw.cloudalbum.eda;

import pl.edu.mimuw.cloudalbum.eda.interfaces.Message;

import java.io.Serializable;

/**
 * Created by tomek on 30.12.15.
 */
public class SignedEvent<E extends Serializable> implements Serializable {
    public SignedEvent(E e) {
        super();
        setMessage(e);
        setHash(String.valueOf(e.hashCode()));
    }

    private E message;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    private String hash;

    public E getMessage() {
        return message;
    }

    public void setMessage(E message) {
        this.message = message;
    }
}
