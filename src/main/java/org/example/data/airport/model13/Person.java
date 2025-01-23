package org.example.data.airport.model13;

import java.io.Serializable;

public abstract class Person implements Serializable {
    private static long curPID = 0;
    protected long pid;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void initProps(String name) {
        this.name = name;
        this.pid = curPID++;
    }
}
