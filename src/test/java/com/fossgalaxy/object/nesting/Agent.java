package com.fossgalaxy.object.nesting;

import com.fossgalaxy.object.annotations.ObjectDef;

/**
 * Created by Piers on 06/05/2017.
 */
public class Agent {

    protected final String name;

    @ObjectDef("Agent")
    public Agent(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }
}
