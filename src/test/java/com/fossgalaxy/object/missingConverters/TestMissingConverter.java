package com.fossgalaxy.object.missingConverters;

import com.fossgalaxy.object.ObjectFinder;
import com.fossgalaxy.object.ObjectFinderBuilder;
import com.fossgalaxy.object.exceptions.NoConverterInstalledException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by piers on 08/05/17.
 */
public class TestMissingConverter {

    private static ObjectFinder<TestMissingConverterObject> finder;

    @BeforeClass
    public static void setup() {
        finder = new ObjectFinderBuilder<>(TestMissingConverterObject.class).build();
    }

    @Test(expected = NoConverterInstalledException.class)
    public void testMissingConverter() {
        TestMissingConverterObject object = finder.buildObject("TestMissingConverterObject", "Name");
    }
}
