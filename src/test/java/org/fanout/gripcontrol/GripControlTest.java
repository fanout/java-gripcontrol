package org.fanout.gripcontrol;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.*;

import static org.junit.Assert.*;

public class GripControlTest {
    @Test
    public void testCreateHold() throws UnsupportedEncodingException {
        JsonParser parser = new JsonParser();
        List<Channel> channels = new ArrayList<Channel>();
        channels.add(new Channel("chan1"));
        String hold = GripControl.createHold("mode", channels, null, 0);
        JsonElement jsonElement1 = parser.parse("{\"hold\":{\"mode\":\"mode\",\"channels\":[{\"name\":\"chan1\"}]}}");
        JsonElement jsonElement2 = parser.parse(hold);
        assertEquals(jsonElement1, jsonElement2);
        channels.add(new Channel("chan2", "prev-id"));
        hold = GripControl.createHold("mode", channels, null, 0);
        jsonElement1 = parser.parse("{\"hold\":{\"mode\":\"mode\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}]}}");
        jsonElement2 = parser.parse(hold);
        assertEquals(jsonElement1, jsonElement2);
        Response response = new Response("body");
        hold = GripControl.createHold("mode", channels, response, 0);
        jsonElement1 = parser.parse("{\"response\":{\"body\":\"body\"},\"hold\":{\"mode\":\"mode\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}]}}");
        jsonElement2 = parser.parse(hold);
        assertEquals(jsonElement1, jsonElement2);
        hold = GripControl.createHold("mode", channels, response, 5);
        jsonElement1 = parser.parse("{\"response\":{\"body\":\"body\"},\"hold\":{\"mode\":\"mode\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}],\"timeout\":5}}");
        jsonElement2 = parser.parse(hold);
        assertEquals(jsonElement1, jsonElement2);
    }

    @Test
    public void testCreateHoldResponse() throws UnsupportedEncodingException {
        JsonParser parser = new JsonParser();
        List<Channel> channels = new ArrayList<Channel>();
        channels.add(new Channel("chan1"));
        String hold = GripControl.createHoldResponse(channels);
        JsonElement jsonElement1 = parser.parse("{\"hold\":{\"mode\":\"response\",\"channels\":[{\"name\":\"chan1\"}]}}");
        JsonElement jsonElement2 = parser.parse(hold);
        assertEquals(jsonElement1, jsonElement2);
        channels.add(new Channel("chan2", "prev-id"));
        hold = GripControl.createHoldResponse(channels);
        jsonElement1 = parser.parse("{\"hold\":{\"mode\":\"response\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}]}}");
        jsonElement2 = parser.parse(hold);
        assertEquals(jsonElement1, jsonElement2);
        Response response = new Response("body");
        hold = GripControl.createHoldResponse(channels, response);
        jsonElement1 = parser.parse("{\"response\":{\"body\":\"body\"},\"hold\":{\"mode\":\"response\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}]}}");
        jsonElement2 = parser.parse(hold);
        assertEquals(jsonElement1, jsonElement2);
        hold = GripControl.createHoldResponse(channels, response, 5);
        jsonElement1 = parser.parse("{\"response\":{\"body\":\"body\"},\"hold\":{\"mode\":\"response\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}],\"timeout\":5}}");
        jsonElement2 = parser.parse(hold);
        assertEquals(jsonElement1, jsonElement2);
    }

    @Test
    public void testCreateHoldStream() throws UnsupportedEncodingException {
        JsonParser parser = new JsonParser();
        List<Channel> channels = new ArrayList<Channel>();
        channels.add(new Channel("chan1"));
        String hold = GripControl.createHoldStream(channels);
        JsonElement jsonElement1 = parser.parse("{\"hold\":{\"mode\":\"stream\",\"channels\":[{\"name\":\"chan1\"}]}}");
        JsonElement jsonElement2 = parser.parse(hold);
        assertEquals(jsonElement1, jsonElement2);
        channels.add(new Channel("chan2", "prev-id"));
        hold = GripControl.createHoldStream(channels);
        jsonElement1 = parser.parse("{\"hold\":{\"mode\":\"stream\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}]}}");
        jsonElement2 = parser.parse(hold);
        assertEquals(jsonElement1, jsonElement2);
        Response response = new Response("body");
        hold = GripControl.createHoldStream(channels, response);
        jsonElement1 = parser.parse("{\"response\":{\"body\":\"body\"},\"hold\":{\"mode\":\"stream\",\"channels\":[{\"name\":\"chan1\"},{\"name\":\"chan2\",\"prev-id\":\"prev-id\"}]}}");
        jsonElement2 = parser.parse(hold);
        assertEquals(jsonElement1, jsonElement2);
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
    public void testValidateSig() {
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

    @Test
    public void testEncodeWebSocketEvents() {
        List<WebSocketEvent> events = new ArrayList<WebSocketEvent>();
        events.add(new WebSocketEvent("TEXT", "Hello"));
        events.add(new WebSocketEvent("TEXT", ""));
        events.add(new WebSocketEvent("TEXT"));
        assertEquals(GripControl.encodeWebSocketEvents(events), "TEXT 5\r\nHello\r\nTEXT 0\r\n\r\nTEXT\r\n");
        events = new ArrayList<WebSocketEvent>();
        events.add(new WebSocketEvent("OPEN"));
        assertEquals(GripControl.encodeWebSocketEvents(events), "OPEN\r\n");
    }

    @Test
    public void testEncodeWebSocketEventsUnicode() {
        List<WebSocketEvent> events = new ArrayList<WebSocketEvent>();
        events.add(new WebSocketEvent("TEXT", "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל"));
        assertEquals(GripControl.encodeWebSocketEvents(events), "TEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\n");

        events = new ArrayList<WebSocketEvent>();
        events.add(new WebSocketEvent("TEXT", "😀 Grinning Face.\n😃 Grinning Face with Big Eyes.\n😄 Grinning Face with Smiling Eyes.\n😁 Beaming Face with Smiling Eyes.\n😆 Grinning Squinting Face.\n😅 Grinning Face with Sweat.\n🤣 Rolling on the Floor Laughing.\n😂 Face with Tears of Joy."));
        assertEquals(GripControl.encodeWebSocketEvents(events), "TEXT fe\r\n😀 Grinning Face.\n😃 Grinning Face with Big Eyes.\n😄 Grinning Face with Smiling Eyes.\n😁 Beaming Face with Smiling Eyes.\n😆 Grinning Squinting Face.\n😅 Grinning Face with Sweat.\n🤣 Rolling on the Floor Laughing.\n😂 Face with Tears of Joy.\r\n");

        events = new ArrayList<WebSocketEvent>();
        events.add(new WebSocketEvent("TEXT", "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל"));
        events.add(new WebSocketEvent("TEXT", "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל"));
        events.add(new WebSocketEvent("TEXT", "😀 Grinning Face.\n😃 Grinning Face with Big Eyes.\n😄 Grinning Face with Smiling Eyes.\n😁 Beaming Face with Smiling Eyes.\n😆 Grinning Squinting Face.\n😅 Grinning Face with Sweat.\n🤣 Rolling on the Floor Laughing.\n😂 Face with Tears of Joy."));
        events.add(new WebSocketEvent("TEXT", "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל"));
        events.add(new WebSocketEvent("TEXT", "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל"));
        assertEquals(GripControl.encodeWebSocketEvents(events), "TEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\nTEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\nTEXT fe\r\n😀 Grinning Face.\n😃 Grinning Face with Big Eyes.\n😄 Grinning Face with Smiling Eyes.\n😁 Beaming Face with Smiling Eyes.\n😆 Grinning Squinting Face.\n😅 Grinning Face with Sweat.\n🤣 Rolling on the Floor Laughing.\n😂 Face with Tears of Joy.\r\nTEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\nTEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\n");
    }

    @Test
    public void testDecodeWebSocketEvents() throws IllegalArgumentException {
        List<WebSocketEvent> events = GripControl.decodeWebSocketEvents("OPEN\r\nTEXT 5\r\nHello" +
            "\r\nTEXT 0\r\n\r\nCLOSE\r\nTEXT\r\nCLOSE\r\n");
        assertEquals(events.size(), 6);
        assertEquals(events.get(0).type, "OPEN");
        assertEquals(events.get(0).content, null);
        assertEquals(events.get(1).type, "TEXT");
        assertEquals(events.get(1).content, "Hello");
        assertEquals(events.get(2).type, "TEXT");
        assertEquals(events.get(2).content, "");
        assertEquals(events.get(3).type, "CLOSE");
        assertEquals(events.get(3).content, null);
        assertEquals(events.get(4).type, "TEXT");
        assertEquals(events.get(4).content, null);
        assertEquals(events.get(5).type, "CLOSE");
        assertEquals(events.get(5).content, null);
        events = GripControl.decodeWebSocketEvents("OPEN\r\n");
        assertEquals(events.size(), 1);
        assertEquals(events.get(0).type, "OPEN");
        assertEquals(events.get(0).content, null);
        events = GripControl.decodeWebSocketEvents("TEXT 5\r\nHello\r\n");
        assertEquals(events.size(), 1);
        assertEquals(events.get(0).type, "TEXT");
        assertEquals(events.get(0).content, "Hello");
    }

    @Test
    public void testDecodeWebSocketEventsUnicode() {
        List<WebSocketEvent> events = GripControl.decodeWebSocketEvents("TEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\n");
        assertEquals(events.size(), 1);
        assertEquals(events.get(0).type, "TEXT");
        assertEquals(events.get(0).content, "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל");

        events = GripControl.decodeWebSocketEvents("TEXT fe\r\n😀 Grinning Face.\n😃 Grinning Face with Big Eyes.\n😄 Grinning Face with Smiling Eyes.\n😁 Beaming Face with Smiling Eyes.\n😆 Grinning Squinting Face.\n😅 Grinning Face with Sweat.\n🤣 Rolling on the Floor Laughing.\n😂 Face with Tears of Joy.\r\n");
        assertEquals(events.size(), 1);
        assertEquals(events.get(0).type, "TEXT");
        assertEquals(events.get(0).content, "😀 Grinning Face.\n😃 Grinning Face with Big Eyes.\n😄 Grinning Face with Smiling Eyes.\n😁 Beaming Face with Smiling Eyes.\n😆 Grinning Squinting Face.\n😅 Grinning Face with Sweat.\n🤣 Rolling on the Floor Laughing.\n😂 Face with Tears of Joy.");

        events = GripControl.decodeWebSocketEvents("TEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\nTEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\nTEXT fe\r\n😀 Grinning Face.\n😃 Grinning Face with Big Eyes.\n😄 Grinning Face with Smiling Eyes.\n😁 Beaming Face with Smiling Eyes.\n😆 Grinning Squinting Face.\n😅 Grinning Face with Sweat.\n🤣 Rolling on the Floor Laughing.\n😂 Face with Tears of Joy.\r\nTEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\nTEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\n");
        assertEquals(events.size(), 5);
        assertEquals(events.get(0).type, "TEXT");
        assertEquals(events.get(0).content, "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל");
        assertEquals(events.get(1).type, "TEXT");
        assertEquals(events.get(1).content, "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל");
        assertEquals(events.get(2).type, "TEXT");
        assertEquals(events.get(2).content, "😀 Grinning Face.\n😃 Grinning Face with Big Eyes.\n😄 Grinning Face with Smiling Eyes.\n😁 Beaming Face with Smiling Eyes.\n😆 Grinning Squinting Face.\n😅 Grinning Face with Sweat.\n🤣 Rolling on the Floor Laughing.\n😂 Face with Tears of Joy.");
        assertEquals(events.get(3).type, "TEXT");
        assertEquals(events.get(3).content, "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל");
        assertEquals(events.get(4).type, "TEXT");
        assertEquals(events.get(4).content, "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל");
    }

    @Test
    public void testDecodeWebSocketEventsArray() {
        String eventString = "TEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\n";
        byte[] eventBytes = eventString.getBytes();
        List<WebSocketEvent> events = GripControl.decodeWebSocketEvents(eventBytes);
        assertEquals(events.size(), 1);
        assertEquals(events.get(0).type, "TEXT");
        assertEquals(events.get(0).content, "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל");

        eventString = "TEXT fe\r\n😀 Grinning Face.\n😃 Grinning Face with Big Eyes.\n😄 Grinning Face with Smiling Eyes.\n😁 Beaming Face with Smiling Eyes.\n😆 Grinning Squinting Face.\n😅 Grinning Face with Sweat.\n🤣 Rolling on the Floor Laughing.\n😂 Face with Tears of Joy.\r\n";
        eventBytes = eventString.getBytes();
        events = GripControl.decodeWebSocketEvents(eventBytes);
        assertEquals(events.size(), 1);
        assertEquals(events.get(0).type, "TEXT");
        assertEquals(events.get(0).content, "😀 Grinning Face.\n😃 Grinning Face with Big Eyes.\n😄 Grinning Face with Smiling Eyes.\n😁 Beaming Face with Smiling Eyes.\n😆 Grinning Squinting Face.\n😅 Grinning Face with Sweat.\n🤣 Rolling on the Floor Laughing.\n😂 Face with Tears of Joy.");

        eventString = "TEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\nTEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\nTEXT fe\r\n😀 Grinning Face.\n😃 Grinning Face with Big Eyes.\n😄 Grinning Face with Smiling Eyes.\n😁 Beaming Face with Smiling Eyes.\n😆 Grinning Squinting Face.\n😅 Grinning Face with Sweat.\n🤣 Rolling on the Floor Laughing.\n😂 Face with Tears of Joy.\r\nTEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\nTEXT 69\r\n😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל\r\n";
        eventBytes = eventString.getBytes();
        events = GripControl.decodeWebSocketEvents(eventBytes);
        assertEquals(events.size(), 5);
        assertEquals(events.get(0).type, "TEXT");
        assertEquals(events.get(0).content, "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל");
        assertEquals(events.get(1).type, "TEXT");
        assertEquals(events.get(1).content, "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל");
        assertEquals(events.get(2).type, "TEXT");
        assertEquals(events.get(2).content, "😀 Grinning Face.\n😃 Grinning Face with Big Eyes.\n😄 Grinning Face with Smiling Eyes.\n😁 Beaming Face with Smiling Eyes.\n😆 Grinning Squinting Face.\n😅 Grinning Face with Sweat.\n🤣 Rolling on the Floor Laughing.\n😂 Face with Tears of Joy.");
        assertEquals(events.get(3).type, "TEXT");
        assertEquals(events.get(3).content, "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל");
        assertEquals(events.get(4).type, "TEXT");
        assertEquals(events.get(4).content, "😍Smiling Face with Heart-Shaped Eyes☼Sun✂︎Scissors💖Heart⛺️sunset🇮🇱דגל ישראל");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDecodeWebSocketEventsException1() throws IllegalArgumentException {
        GripControl.decodeWebSocketEvents("TEXT 5");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDecodeWebSocketEventsException2() throws IllegalArgumentException {
        GripControl.decodeWebSocketEvents("OPEN\r\nTEXT");
    }
}
