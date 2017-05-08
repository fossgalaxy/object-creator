package com.fossgalaxy.object.nesting;

import com.fossgalaxy.object.ObjectFinder;
import com.fossgalaxy.object.ObjectFinderBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by Piers on 06/05/2017.
 */
public class TestNestedAgent {

    private static ObjectFinder<Agent> finder;

    @BeforeClass
    public static void setup(){
        finder = new ObjectFinderBuilder<>(Agent.class).build();
    }

    @Test
    public void testSimpleNestedCase(){
        Agent agent = finder.buildObject("iggi[peanut:Agent[first]:butter]");
        assertNotNull(agent);
        assertEquals("peanut", agent.getName());
        assertEquals(IGGIAgent.class, agent.getClass());
        IGGIAgent iggiAgent = (IGGIAgent) agent;
        assertEquals(Agent.class, iggiAgent.getAgent().getClass());
        assertEquals("first", iggiAgent.getAgent().getName());
        assertEquals("butter", iggiAgent.getOther());

    }



}
