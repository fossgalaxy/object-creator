package com.fossgalaxy.object;

import org.junit.Test;

/**
 * Created by piers on 05/05/17.
 */
public class UnknownObject {

    private final String name;

    public UnknownObject(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UnknownObject{" +
                "name='" + name + '\'' +
                '}';
    }
}
