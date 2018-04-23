package com.fossgalaxy.object.sameTag;

import com.fossgalaxy.object.ObjectFinder;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

public class TestTestTagObject {

    private static ObjectFinder<TestTagObject> finder;

    @BeforeClass
    public static void setup(){
        finder = new ObjectFinder.Builder<>(TestTagObject.class).build();
    }

    @Test
    public void testOneArg(){
        TestTagObject object = finder.buildObject("TestTag", "Data");
        assertNotNull(object);
    }

    @Test
    public void testTwoArg(){
        TestTagObject object = finder.buildObject("TestTag", "Data", "Suffix");
        assertNotNull(object);
    }
}
