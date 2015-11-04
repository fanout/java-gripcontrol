import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import org.fanout.gripcontrol.*;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

public class GripControlTest {
    @Test
    public void testCreateHold() throws UnsupportedEncodingException {
        List<Channel> channels = new ArrayList<Channel>();
        channels.add(new Channel("chan1"));
        String hold = GripControl.createHold("mode", channels, null, 0);
        assertEquals(hold, "{\"hold\":{\"mode\":\"mode\",\"channels\":[{\"name\":\"chan1\"}]}}");
        channels.add(new Channel("chan2", "prev-id"));
        hold = GripControl.createHold("mode", channels, null, 0);
        assertEquals(hold, "{\"hold\":{\"mode\":\"mode\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}]}}");
        Response response = new Response("body");
        hold = GripControl.createHold("mode", channels, response, 0);
        assertEquals(hold, "{\"response\":{\"body\":\"body\"},\"hold\":{\"mode\":\"mode\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}]}}");
        hold = GripControl.createHold("mode", channels, response, 5);
        assertEquals(hold, "{\"response\":{\"body\":\"body\"},\"hold\":{\"mode\":\"mode\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}],\"timeout\":5}}");
    }

    @Test
    public void testCreateHoldResponse() throws UnsupportedEncodingException {
        List<Channel> channels = new ArrayList<Channel>();
        channels.add(new Channel("chan1"));
        String hold = GripControl.createHoldResponse(channels);
        assertEquals(hold, "{\"hold\":{\"mode\":\"response\",\"channels\":[{\"name\":\"chan1\"}]}}");
        channels.add(new Channel("chan2", "prev-id"));
        hold = GripControl.createHoldResponse(channels);
        assertEquals(hold, "{\"hold\":{\"mode\":\"response\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}]}}");
        Response response = new Response("body");
        hold = GripControl.createHoldResponse(channels, response);
        assertEquals(hold, "{\"response\":{\"body\":\"body\"},\"hold\":{\"mode\":\"response\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}]}}");
        hold = GripControl.createHoldResponse(channels, response, 5);
        assertEquals(hold, "{\"response\":{\"body\":\"body\"},\"hold\":{\"mode\":\"response\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}],\"timeout\":5}}");
    }

    @Test
    public void testCreateHoldStream() throws UnsupportedEncodingException {
        List<Channel> channels = new ArrayList<Channel>();
        channels.add(new Channel("chan1"));
        String hold = GripControl.createHoldStream(channels);
        assertEquals(hold, "{\"hold\":{\"mode\":\"stream\",\"channels\":[{\"name\":\"chan1\"}]}}");
        channels.add(new Channel("chan2", "prev-id"));
        hold = GripControl.createHoldStream(channels);
        assertEquals(hold, "{\"hold\":{\"mode\":\"stream\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}]}}");
        Response response = new Response("body");
        hold = GripControl.createHoldStream(channels, response);
        assertEquals(hold, "{\"response\":{\"body\":\"body\"},\"hold\":{\"mode\":\"stream\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}]}}");
    }

    @Test
    public void testCreateGripChannelHeader() {
        List<Channel> channels = new ArrayList<Channel>();
        channels.add(new Channel("chan1"));
        String header = GripControl.createGripChannelHeader(channels);
        assertEquals(header, "chan1");
        channels.add(new Channel("chan2", "prev-id"));
        header = GripControl.createGripChannelHeader(channels);
        assertEquals(header, "chan1, chan2; prev-id=prev-id");
    }

    @Test
    public void testWebSocketControlMessage() {
        String message = GripControl.webSocketControlMessage("type", null);
        assertEquals(message, "{\"type\":\"type\"}");
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("arg1", "value1");
        args.put("arg2", "value2");
        message = GripControl.webSocketControlMessage("type", args);
        assertEquals(message, "{\"arg2\":\"value2\",\"arg1\":\"value1\",\"type\":\"type\"}");
    }

    @Test
    public void testParseGripUri() throws UnsupportedEncodingException, MalformedURLException {
        String url = "http://test.com/path";
        Map<String, Object> parsedUri = GripControl.parseGripUri(url);
        assertEquals(parsedUri.get("control_uri"), "http://test.com/path");
        url = "http://test.com/path?arg1=val1";
        parsedUri = GripControl.parseGripUri(url);
        assertEquals(parsedUri.get("control_uri"), "http://test.com/path?arg1=val1");
        url = "http://test.com:8900/path?arg1=val1&arg2=val2";
        parsedUri = GripControl.parseGripUri(url);
        assertEquals(parsedUri.get("control_uri"), "http://test.com:8900/path?arg1=val1&arg2=val2");
        url = "http://test.com:8900/path?arg1=val1&arg2=val2&iss=claim";
        parsedUri = GripControl.parseGripUri(url);
        assertEquals(parsedUri.get("control_uri"), "http://test.com:8900/path?arg1=val1&arg2=val2");
        assertEquals(parsedUri.get("control_iss"), "claim");
        url = "http://test.com:8900/path?arg1=val1&arg2=val2&iss=claim&key=keyval";
        parsedUri = GripControl.parseGripUri(url);
        assertEquals(parsedUri.get("control_uri"), "http://test.com:8900/path?arg1=val1&arg2=val2");
        assertEquals(parsedUri.get("control_iss"), "claim");
        assertEquals(parsedUri.get("key"), "keyval");
        url = "https://test.com:8900/path?arg1=val1&arg2=val2&iss=claim&key=base64:aGVsbG8=";
        parsedUri = GripControl.parseGripUri(url);
        assertEquals(parsedUri.get("control_uri"), "https://test.com:8900/path?arg1=val1&arg2=val2");
        assertEquals(parsedUri.get("control_iss"), "claim");
        assertEquals(parsedUri.get("key"), "hello");
    }

    @Test
    public void testValidateSig() throws UnsupportedEncodingException, MalformedURLException {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOlsiT25saW5lIEpXVCBCdWlsZGVyIiwidGVzdGlzcy" +
                "JdLCJpYXQiOjk3MzI5NTg4NSwiZXhwIjoyNTUxMDQ2Mjg1LCJhdWQiOiJ3d3cuZXhhbXBsZS5jb20iLCJzdWIiOiJq" +
                "cm9ja2V0QGV4YW1wbGUuY29tIn0.Wmm-ulXbOun3egbdqmxjCqegyYu8Tr5MAaguie4rmTE";
        String key = "a2V5";
        assertTrue(GripControl.validateSig(token, key));
        token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOlsiT25saW5lIEpXVCBCdWlsZGVyIiwidGVzdGlzcy" +
                "JdLCJpYXQiOjk3MzI5NTg4NSwiZXhwIjoyNTUxMDQ2Mjg1LCJhdWQiOiJ3d3cuZXhhbXBsZS5jb20iLCJzdWIiOiJq" +
                "cm9ja2V0QGV4YW1wbGUuY29tIn0.Wmm-ulXbOun3egbdqmxjCqegyYu8Tr5MAaguie4rmTE";
        key = "d3JvbmdrZXk=";
        assertFalse(GripControl.validateSig(token, key));
        token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOlsiT25saW5lIEpXVCBCdWlsZGVyIiwi" +
                "dGVzdGlzcyJdLCJpYXQiOjk3MzI5NTg4NSwiZXhwIjoxMDA0NzQ1NDg1LCJhdWQiOiJ3d3cuZXhhbXBsZS5jb20iLC" +
                "JzdWIiOiJqcm9ja2V0QGV4YW1wbGUuY29tIn0.W8e4mxvbMKuotkINOyZX5jDO7KFD-jpgPHbWNGV5CHQ";
        key = "a2V5";
        assertFalse(GripControl.validateSig(token, key));
    }
}
