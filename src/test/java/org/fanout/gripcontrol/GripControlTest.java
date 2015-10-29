import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import org.fanout.gripcontrol.*;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;

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
}
