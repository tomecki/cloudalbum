package pl.edu.mimuw.cloudalbum.contracts;

import java.io.Serializable;

/**
 * Created by tomek on 18.01.16.
 */
public class GetZMIContract implements Serializable {
    private String zonePath;

    public String getZonePath() {
        return zonePath;
    }

    public void setZonePath(String zonePath) {
        this.zonePath = zonePath;
    }

    public GetZMIContract(String zonePath) {

        this.zonePath = zonePath;
    }

    public GetZMIContract() {

    }
}
