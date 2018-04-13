package com.fossgalaxy.object.testcomplex;

import com.fossgalaxy.object.ObjectFinder;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by Piers on 06/05/2017.
 */
public class TestComplexObject {

    private static ObjectFinder<ComplexTestObject> finder;

    @BeforeClass
    public static void setup(){
        finder = new ObjectFinder.Builder<>(ComplexTestObject.class).setCache("cache.xml").build();
    }

    @Test
    public void testCreate(){
        ComplexTestObject object = finder.buildObject("Complex",
               "Name",
                "1",
                "2",
                "2.4",
                "2.5",
                "4.5",
                "5.5",
                "true",
                "false",
                "1,2,3",
                "1,2,3",
                "2.1,2.2,2.3",
                "2.4,2.5,2.6",
                "2.1,2.2,2.3",
                "2.1,2.2,2.3",
                "true,false",
                "true,false",
                "test,Test,test",
                "1",
                "1",
                "1,2",
                "3,4",
                "COMPLEX"
                );

        assertNotNull(object);
    }
}
