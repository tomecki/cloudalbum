package pl.edu.mimuw.cloudalbum.contracts;

import java.io.Serializable;

/**
 * Created by tomek on 13.01.16.
 */
public class InstallQueryContract implements Serializable {
    private String query;
    private String queryName;

    public InstallQueryContract() {
    }

    public InstallQueryContract(String queryName, String query) {
        this.query = query;
        this.queryName = queryName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }
}
