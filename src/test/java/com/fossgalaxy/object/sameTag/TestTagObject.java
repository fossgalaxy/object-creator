package com.fossgalaxy.object.sameTag;

import com.fossgalaxy.object.annotations.ObjectDef;
import com.fossgalaxy.object.annotations.ObjectDefStatic;

public class TestTagObject {

    private final String data;

    @ObjectDef("TestTagObject")
    public TestTagObject(String data) {
        this.data = data;
    }

    @ObjectDef("TestTagObject")
    public TestTagObject() {
        this.data = "";
    }

    @ObjectDefStatic("TestTag")
    public static TestTagObject getSimple(String data){
        return new TestTagObject(data);
    }

    @ObjectDefStatic("TestTag")
    public static TestTagObject getArray(String data, String suffix){
        return new TestTagObject(data + suffix);
    }

    @ObjectDefStatic("TestTag")
    public static TestTagObject getArrays(String data, String suffix){
        return new TestTagObject(data + suffix);
    }
}
