package org.fanout.gripcontrol;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ResponseTest {
    @Test
    public void testResponse() throws UnsupportedEncodingException {
        Response response = new Response("body");
        assertEquals(new String(response.body), "body");
        assertEquals(response.headers, null);
        assertEquals(response.code, null);
        assertEquals(response.reason, null);
        Map<String, String> headers = new HashMap<String, String>();
        response = new Response("body", headers);
        assertEquals(new String(response.body), "body");
        assertEquals(response.headers, headers);
        assertEquals(response.code, null);
        assertEquals(response.reason, null);
        response = new Response("body", headers, "code", "reason");
        assertEquals(new String(response.body), "body");
        assertEquals(response.headers, headers);
        assertEquals(response.code, "code");
        assertEquals(response.reason, "reason");
        response = new Response("body".getBytes("utf-8"));
        assertEquals(new String(response.body), "body");
        assertEquals(response.headers, null);
        assertEquals(response.code, null);
        assertEquals(response.reason, null);
        headers = new HashMap<String, String>();
        response = new Response("body".getBytes("utf-8"), headers);
        assertEquals(new String(response.body), "body");
        assertEquals(response.headers, headers);
        assertEquals(response.code, null);
        assertEquals(response.reason, null);
        response = new Response("body".getBytes("utf-8"), headers, "code", "reason");
        assertEquals(new String(response.body), "body");
        assertEquals(response.headers, headers);
        assertEquals(response.code, "code");
        assertEquals(response.reason, "reason");
    }
}
