package com.fossgalaxy.object;


import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by piers on 05/05/17.
 */
public class TestObjectFinder {

    private static ObjectFinder<TestObject> finder;

    @BeforeClass
    public static void setup(){
        finder = new ObjectFinder<>(TestObject.class);
    }

    @Test
    public void test(){
        TestObject testObject = finder.buildObject("Object", "Peanut");
        assertNotNull(testObject);
    }

    @Test
    public void testStatic(){
        TestObject testObject = finder.buildObject("Name");
        assertNotNull(testObject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonExistingObject(){
        TestObject testObject = finder.buildObject("peanut");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongArgumentCount(){
        TestObject testObject = finder.buildObject("Object", "Peanut", "Peanut");
    }

    @Test
    public void testDelegatedFunction(){
        TestObject testObject = finder.buildObject("WithUnknown", "Name", "Peanut");
        assertNotNull(testObject);
    }

}
