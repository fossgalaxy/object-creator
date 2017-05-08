package com.fossgalaxy.object.nesting;

import com.fossgalaxy.object.annotations.ObjectDef;

/**
 * Created by Piers on 06/05/2017.
 */
public class IGGIAgent extends Agent {
    private final Agent agent;
    private final String other;

    @ObjectDef("iggi")
    public IGGIAgent(String name, Agent agent, String other) {
        super(name);
        this.agent = agent;
        this.other = other;
    }

    @Override
    public String toString() {
        return "IGGIAgent{" +
                "name='" + name + '\'' +
                ", agent=" + agent +
                ", other='" + other + '\'' +
                '}';
    }

    public Agent getAgent() {
        return agent;
    }

    public String getOther() {
        return other;
    }
}
