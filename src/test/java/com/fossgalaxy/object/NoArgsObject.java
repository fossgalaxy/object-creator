package com.fossgalaxy.object;

/**
 * Created by Piers on 06/05/2017.
 */
public class NoArgsObject {

    private final String name;

    public NoArgsObject() {
        this.name = "";
    }

    @Override
    public String toString() {
        return "NoArgsObject{" +
                "name='" + name + '\'' +
                '}';
    }
}
