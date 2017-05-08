package com.fossgalaxy.object.missingConverters;

import com.fossgalaxy.object.TestObject;
import com.fossgalaxy.object.annotations.ObjectDef;

/**
 * Created by piers on 08/05/17.
 */
public class TestMissingConverterObject {

    private final TestObject testObject;

    @ObjectDef("TestMissingConverterObject")
    public TestMissingConverterObject(TestObject testObject) {
        this.testObject = testObject;
    }
}
