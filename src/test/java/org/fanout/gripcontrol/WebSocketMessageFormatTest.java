import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import org.fanout.gripcontrol.*;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;

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
