import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import org.fanout.gripcontrol.*;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;

public class HttpResponseFormatTest {
    @Test
    public void testHttpResponseFormat() throws UnsupportedEncodingException {
        HttpResponseFormat format = new HttpResponseFormat("body");
        assertEquals(format.name(), "http-response");
        Map<String, Object> export = (Map<String, Object>)format.export();
        assertEquals(export.get("body"), "body");
        format = new HttpResponseFormat("body".getBytes("utf-8"));
        export = (Map<String, Object>)format.export();
        assertEquals(export.get("body"), "body");
        byte[] byteData = {(byte) Integer.parseInt("10001111", 2), (byte) Integer.parseInt("10111111", 2)};
        format = new HttpResponseFormat(byteData);
        export = (Map<String, Object>)format.export();
        assertEquals(export.get("body-bin"), DatatypeConverter.printBase64Binary(byteData));
        Map<String, String> headers = new HashMap<String, String>();
        format = new HttpResponseFormat("body", headers, "code", "reason");
        assertEquals(format.name(), "http-response");
        export = (Map<String, Object>)format.export();
        assertEquals(export.get("code"), "code");
        assertEquals(export.get("headers"), headers);
        assertEquals(export.get("reason"), "reason");
        assertEquals(export.get("body"), "body");
        format = new HttpResponseFormat("body".getBytes("utf-8"), headers, "code", "reason");
        export = (Map<String, Object>)format.export();
        assertEquals(export.get("code"), "code");
        assertEquals(export.get("headers"), headers);
        assertEquals(export.get("reason"), "reason");
        assertEquals(export.get("body"), "body");
        format = new HttpResponseFormat(byteData, headers, "code", "reason");
        export = (Map<String, Object>)format.export();
        assertEquals(export.get("code"), "code");
        assertEquals(export.get("headers"), headers);
        assertEquals(export.get("reason"), "reason");
        assertEquals(export.get("body-bin"), DatatypeConverter.printBase64Binary(byteData));
    }
}
