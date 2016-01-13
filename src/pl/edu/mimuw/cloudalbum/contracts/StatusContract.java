package pl.edu.mimuw.cloudalbum.contracts;

import java.io.Serializable;

/**
 * Created by tomek on 13.01.16.
 */
public class StatusContract implements Serializable {
    public StatusContract(STATUS status) {
        this.status = status;
    }

    public enum STATUS {
        OK, ERROR
    };

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    private STATUS status;
}
