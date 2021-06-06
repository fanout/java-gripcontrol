package org.fanout.gripcontrol;

import org.junit.Test;

import static org.junit.Assert.*;

public class WebSocketEventTest {
    @Test
    public void testWebSocketEvent() {

        // Actual data is backed by binary bytes
        WebSocketEvent event = new WebSocketEvent( "type" );
        assertEquals(event.type, "type");
        assertNull(event.content);

        event = new WebSocketEvent( "type", "Hello" );
        assertEquals(event.type, "type");
        assertEquals(event.content, "Hello");
        assertArrayEquals(event.contentBytes, new byte[] { 0x48, 0x65, 0x6c, 0x6c, 0x6f });

        event = new WebSocketEvent("type", new byte[] { 0x48, 0x65, 0x6c, 0x6c, 0x6f });
        assertEquals(event.type, "type");
        assertEquals(event.content, "Hello");
        assertArrayEquals(event.contentBytes, new byte[] { 0x48, 0x65, 0x6c, 0x6c, 0x6f });
    }
}
