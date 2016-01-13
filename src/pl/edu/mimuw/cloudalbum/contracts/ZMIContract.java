package pl.edu.mimuw.cloudalbum.contracts;

import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.io.Serializable;

/**
 * Created by tomek on 13.01.16.
 */
public class ZMIContract implements Serializable {
    private ZMI zmi;
    private long timeInMilis;

    public ZMIContract() {
    }

    public ZMIContract(ZMI zmi, long timeInMilis) {
        this.zmi = zmi;
        this.timeInMilis = timeInMilis;
    }

    public ZMI getZmi() {
        return zmi;
    }

    public void setZmi(ZMI zmi) {
        this.zmi = zmi;
    }

    public long getTimeInMilis() {
        return timeInMilis;
    }

    public void setTimeInMilis(long timeInMilis) {
        this.timeInMilis = timeInMilis;
    }
}
