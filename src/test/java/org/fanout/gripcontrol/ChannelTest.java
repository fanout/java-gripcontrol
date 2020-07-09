package org.fanout.gripcontrol;

import org.junit.Test;
import static org.junit.Assert.*;

public class ChannelTest {
    @Test
    public void testChannel() {
        Channel chan = new Channel("name", "prevId");
        assertEquals(chan.name, "name");
        assertEquals(chan.prevId, "prevId");
        chan = new Channel("name");
        assertEquals(chan.name, "name");
        assertEquals(chan.prevId, null);
    }
}
