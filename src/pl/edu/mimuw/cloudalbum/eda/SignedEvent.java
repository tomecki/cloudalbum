package pl.edu.mimuw.cloudalbum.eda;

import pl.edu.mimuw.cloudalbum.eda.interfaces.Message;

/**
 * Created by tomek on 30.12.15.
 */
public class SignedEvent extends Event {
    private SignedEvent(Event m) {
        super();
        setHash(String.valueOf(m.hashCode()));
    }

    public static SignedEvent sign(Event m){
        return new SignedEvent(m);
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    private String hash;
}
