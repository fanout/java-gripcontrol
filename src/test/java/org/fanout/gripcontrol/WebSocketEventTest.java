import org.junit.Test;
import static org.junit.Assert.*;

import org.fanout.gripcontrol.*;

public class WebSocketEventTest {
    @Test
    public void testWebSocketEvent() {
        WebSocketEvent event = new WebSocketEvent("type");
        assertEquals(event.type, "type");
        assertEquals(event.content, null);
        event = new WebSocketEvent("type2", "content");
        assertEquals(event.type, "type2");
        assertEquals(event.content, "content");
    }
}
