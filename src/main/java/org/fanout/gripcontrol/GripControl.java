//    GripControl.java
//    ~~~~~~~~~
//    This module implements the GripControl class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.gripcontrol;

import java.util.*;
import org.fanout.pubcontrol.*;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import com.google.gson.Gson;

/**
 * This class and its features are used in conjunction with GRIP proxies.
 * This includes facilitating the creation of hold instructions for HTTP
 * long-polling and HTTP streaming, parsing GRIP URIs into config objects,
 * validating the GRIP-SIG header coming from GRIP proxies, creating GRIP
 * channel headers, and also WebSocket-over-HTTP features such as
 * encoding/decoding web socket events and generating control messages.
 */
public class GripControl {
    /**
     * Create GRIP hold instructions for the specified parameters including a timeout.
     */
    public String createHold(String mode, List<Channel> channels, Response response, int timeout) {
        Map<String, Object> hold = new HashMap<String, Object>();
        hold.put("mode", mode);
        List<Map<String, Object>> holdChannels = getHoldChannels(channels);
        hold.put("channels", holdChannels);
        if (timeout > 0)
            hold.put("timeout", timeout);
        Map<String, Object> holdResponse = getHoldResponse(response);
        Map<String, Object> instruct = new HashMap<String, Object>();
        if (holdResponse != null)
            instruct.put("response", holdResponse);
        return new Gson().toJson(instruct);
    }


    /**
     * Get an array of hashes representing the specified channels parameter.
     * The resulting array is used for creating GRIP proxy hold instructions.
     */
    private List<Map<String, Object>> getHoldChannels(List<Channel> channels) {
        List<Map<String, Object>> holdChannels = new ArrayList<Map<String, Object>>();
        for (Channel channel : channels) {
            Map<String, Object> holdChannel = new HashMap<String, Object>();
            holdChannel.put("name", channel.name);
            if (channel.prevId != null)
                holdChannel.put("prev-id", channel.prevId);
            holdChannels.add(holdChannel);
        }
        return holdChannels;
    }

    /**
     * Get a hash representing the specified response parameter.
     * The resulting hash is used for creating GRIP proxy hold instructions.
     */
    private Map<String, Object> getHoldResponse(Response response) {
        if (response == null)
            return null;
        Map<String, Object> holdResponse = new HashMap<String, Object>();
        if (response.code != null)
            holdResponse.put("code", response.code);
        if (response.reason != null)
            holdResponse.put("reason", response.reason);
        if (response.headers != null)
            holdResponse.put("headers", response.headers);
        if (response.body != null) {
            if (Utilities.isUtf8(response.body)) {
                try {
                    holdResponse.put("body", new String(response.body, "utf-8"));
                } catch (UnsupportedEncodingException e) { }
            } else {
                holdResponse.put("body-bin", DatatypeConverter.printBase64Binary(response.body));
            }
        }
        return holdResponse;
    }

    /**
     * Create a GRIP channel header for the specified channels.
     * The returned GRIP channel header is used when sending instructions to
     * GRIP proxies via HTTP headers.
     */
    public String createGripChannelHeader(List<Channel> channels) {
        String header = "";
        for (Channel channel : channels) {
            if (header != "")
                header = header + ", ";
            header = header + channel.name;
            if (channel.prevId != null)
                header = header + "; prev-id=" + channel.prevId;
        }
        return header;
    }

    /**
     * Create a GRIP hold response for HTTP long-polling.
     * This method simply passes the specified parameters to the
     * createHold method with "response" as the hold mode.
     */
    public String createHoldResponse(List<Channel> channels) {
        return createHold("response", channels, null, 0);
    }

    /**
     * Create a GRIP hold response for HTTP long-polling.
     * This method simply passes the specified parameters to the
     * createHold method with "response" as the hold mode.
     */
    public String createHoldResponse(List<Channel> channels, Response response) {
        return createHold("response", channels, response, 0);
    }

    /**
     * Create a GRIP hold response for HTTP long-polling.
     * This method simply passes the specified parameters to the
     * createHold method with "response" as the hold mode.
     */
    public String createHoldResponse(List<Channel> channels, Response response, int timeout) {
        return createHold("response", channels, response, timeout);
    }

    /**
     * Create a GRIP hold stream for HTTP streaming.
     * This method simply passes the specified parameters to the
     * createHold method with "response" as the hold mode.
     */
    public String createHoldStream(List<Channel> channels) {
        return createHold("stream", channels, null, 0);
    }

    /**
     * Create a GRIP hold stream for HTTP streaming.
     * This method simply passes the specified parameters to the
     * createHold method with "response" as the hold mode.
     */
    public String createHoldStream(List<Channel> channels, Response response) {
        return createHold("stream", channels, response, 0);
    }

    /**
     * Generate a WebSocket control message with the specified type and optional arguments.
     * WebSocket control messages are passed to GRIP proxies and example usage
     * includes subscribing/unsubscribing a WebSocket connection to/from a channel.
     */
    public String websocketControlMessage(String type, Map<String, Object> args) {
        Map<String, Object> out;
        if (args != null) {
            out = args;
        } else {
            out = new HashMap<String, Object>();
        }
        out.put("type", type);
        String message = new Gson().toJson(out);
        out.remove("type", type);
        return message;
    }
}
