package pl.edu.mimuw.cloudalbum.contracts;

import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.ZMI;

import java.io.*;

/**
 * Created by tomek on 13.01.16.
 */
public class ZMIContract implements Serializable {
    private ZMI zmi;
    private Long timeInMilis;
    private PathName sender;

    public PathName getSender() {
        return sender;
    }

    public void setSender(PathName sender) {
        this.sender = sender;
    }

    public ZMIContract(ZMI zmi, Long timeInMilis, PathName sender) {
        this.zmi = zmi;
        this.timeInMilis = timeInMilis;
        this.sender = sender;
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
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(os);
        zmi.printAttributes(printStream);
        printStream.close();
        return "ZMIContract{" +
                "zmi=" + os.toString() +
                ", timeInMilis=" + timeInMilis +
                ", sender=" + sender.getName() +
                '}';
    }
}
