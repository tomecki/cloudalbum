package pl.edu.mimuw.cloudalbum.contracts;

import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.io.Serializable;

/**
 * Created by tomek on 13.01.16.
 */
public class ZMIContract implements Serializable {
    private ZMI zmi;
    private Long timeInMilis;

    public ZMIContract(ZMI zmi, Long timeInMilis) {
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

    public void setTimeInMilis(Long timeInMilis) {
        this.timeInMilis = timeInMilis;
    }

    @Override
    public String toString() {
        return "ZMIContract{" +
                "zmi=" + zmi +
                ", timeInMilis=" + timeInMilis +
                '}';
    }
}
