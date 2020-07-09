package org.fanout.gripcontrol;

import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class WebSocketMessageFormatTest {
    @Test
    public void testWebSocketMessageFormat() throws UnsupportedEncodingException {
        WebSocketMessageFormat format = new WebSocketMessageFormat("content");
        assertEquals(format.name(), "ws-message");
        Map<String, Object> export = (Map<String, Object>)format.export();
        assertEquals(export.get("content"), "content");
        format = new WebSocketMessageFormat("content".getBytes("utf-8"));
        export = (Map<String, Object>)format.export();
        assertEquals(export.get("content-bin"),
                DatatypeConverter.printBase64Binary("content".getBytes("utf-8")));
    }
}
