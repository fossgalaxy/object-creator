package com.fossgalaxy.object;

import com.fossgalaxy.object.annotations.ObjectDef;

/**
 * Created by piers on 05/05/17.
 */
public class TestObjectWrapper extends TestObject{

    private final TestObject object;

    @ObjectDef("Wrapper")
    public TestObjectWrapper(String name, TestObject object) {
        super(name);
        this.object = object;
    }

    @Override
    public String toString() {
        return "TestObjectWrapper{" +
                "object=" + object +
                '}';
    }
}
