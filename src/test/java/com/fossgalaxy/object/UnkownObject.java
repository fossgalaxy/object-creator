package com.fossgalaxy.object;

import org.junit.Test;

/**
 * Created by piers on 05/05/17.
 */
public class UnkownObject {

    private final String name;

    public UnkownObject(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UnkownObject{" +
                "name='" + name + '\'' +
                '}';
    }
}
