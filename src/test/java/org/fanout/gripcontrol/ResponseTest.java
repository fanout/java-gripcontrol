import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import org.fanout.gripcontrol.*;

public class ResponseTest {
    @Test
    public void testResponse() {
        Response response = new Response("body");
        assertEquals(response.body, "body");
        assertEquals(response.headers, null);
        assertEquals(response.code, null);
        assertEquals(response.reason, null);
        Map<String, String> headers = new HashMap<String, String>();
        response = new Response("body", headers);
        assertEquals(response.body, "body");
        assertEquals(response.headers, headers);
        assertEquals(response.code, null);
        assertEquals(response.reason, null);
        response = new Response("body", headers, "code", "reason");
        assertEquals(response.body, "body");
        assertEquals(response.headers, headers);
        assertEquals(response.code, "code");
        assertEquals(response.reason, "reason");
    }
}
