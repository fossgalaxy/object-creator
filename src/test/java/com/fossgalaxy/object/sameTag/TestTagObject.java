package com.fossgalaxy.object.sameTag;

import com.fossgalaxy.object.annotations.ObjectDefStatic;

public class TestTagObject {

    private final String data;

    public TestTagObject(String data) {
        this.data = data;
    }

    @ObjectDefStatic("TestTag")
    public static TestTagObject getSimple(String data){
        return new TestTagObject(data);
    }

    @ObjectDefStatic("TestTag")
    public static TestTagObject getArray(String data, String suffix){
        return new TestTagObject(data + suffix);
    }
}
