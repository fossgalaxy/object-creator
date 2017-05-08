package com.fossgalaxy.object;

import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by Piers on 06/05/2017.
 */
public class TestNoArgsObject {

    private static ObjectFinder<NoArgsObject> finder;

    @BeforeClass
    public static void setup(){
        finder = new ObjectFinder<>(NoArgsObject.class);
    }

    @Test
    public void testNoArgs(){
        NoArgsObject noArgsObject = finder.buildObject("NoArgsObject");
        assertNotNull(noArgsObject);
    }
}
