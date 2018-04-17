package com.fossgalaxy.object.nesting;

import com.fossgalaxy.object.ObjectFinder;
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
        finder = new ObjectFinder.Builder<>(Agent.class).build();
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

    @Test
    public void testSimpleNestedCaseOtherStrings(){
        finder = new ObjectFinder.Builder<>(Agent.class)
                .setParamStart("(")
                .setParamSeparator(",")
                .setParamEnd(")")
                .build();

        Agent agent = finder.buildObject("iggi(peanut,Agent(first),butter)");
        assertNotNull(agent);
        assertEquals("peanut", agent.getName());
        assertEquals(IGGIAgent.class, agent.getClass());
        IGGIAgent iggiAgent = (IGGIAgent) agent;
        assertEquals(Agent.class, iggiAgent.getAgent().getClass());
        assertEquals("first", iggiAgent.getAgent().getName());
        assertEquals("butter", iggiAgent.getOther());
    }



}
