//    GripControl.java
//    ~~~~~~~~~
//    This module implements the GripControl class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.gripcontrol;

import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

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
     * To disable the timeout pass 0.
     */
    public static String createHold(String mode, List<Channel> channels, Response response, int timeout) {
        Map<String, Object> hold = new HashMap<String, Object>();
        hold.put("mode", mode);
        List<Map<String, Object>> holdChannels = getHoldChannels(channels);
        hold.put("channels", holdChannels);
        if (timeout > 0)
            hold.put("timeout", timeout);
        Map<String, Object> instruct = new HashMap<String, Object>();
        instruct.put("hold", hold);
        Map<String, Object> holdResponse = getHoldResponse(response);
        if (holdResponse != null)
            instruct.put("response", holdResponse);
        return new Gson().toJson(instruct);
    }

    /**
     * Create a GRIP hold response for HTTP long-polling.
     * This method simply passes the specified parameters to the
     * createHold method with "response" as the hold mode.
     */
    public static String createHoldResponse(List<Channel> channels) {
        return createHold("response", channels, null, 0);
    }

    /**
     * Create a GRIP hold response for HTTP long-polling.
     * This method simply passes the specified parameters to the
     * createHold method with "response" as the hold mode.
     */
    public static String createHoldResponse(List<Channel> channels, Response response) {
        return createHold("response", channels, response, 0);
    }

    /**
     * Create a GRIP hold response for HTTP long-polling.
     * This method simply passes the specified parameters to the
     * createHold method with "response" as the hold mode.
     */
    public static String createHoldResponse(List<Channel> channels, Response response, int timeout) {
        return createHold("response", channels, response, timeout);
    }

    /**
     * Create a GRIP hold stream for HTTP streaming.
     * This method simply passes the specified parameters to the
     * createHold method with "response" as the hold mode.
     */
    public static String createHoldStream(List<Channel> channels) {
        return createHold("stream", channels, null, 0);
    }

    /**
     * Create a GRIP hold stream for HTTP streaming.
     * This method simply passes the specified parameters to the
     * createHold method with "response" as the hold mode.
     */
    public static String createHoldStream(List<Channel> channels, Response response) {
        return createHold("stream", channels, response, 0);
    }

    /**
     * Create a GRIP channel header for the specified channels.
     * The returned GRIP channel header is used when sending instructions to
     * GRIP proxies via HTTP headers.
     */
    public static String createGripChannelHeader(List<Channel> channels) {
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
     * Generate a WebSocket control message with the specified type and optional arguments.
     * WebSocket control messages are passed to GRIP proxies and example usage
     * includes subscribing/unsubscribing a WebSocket connection to/from a channel.
     */
    public static String webSocketControlMessage(String type) {
        return webSocketControlMessage(type, null);
    }

    /**
     * Generate a WebSocket control message with the specified type and optional arguments.
     * WebSocket control messages are passed to GRIP proxies and example usage
     * includes subscribing/unsubscribing a WebSocket connection to/from a channel.
     */
    public static String webSocketControlMessage(String type, Map<String, Object> args) {
        Map<String, Object> out;
        if (args != null) {
            out = args;
        } else {
            out = new HashMap<String, Object>();
        }
        out.put("type", type);
        String message = new Gson().toJson(out);
        out.remove("type");
        return message;
    }

    /**
     * Parse the specified GRIP URI into a config object.
     * The URI can include "iss" and "key" JWT authentication query parameters
     * as well as any other required query string parameters. The JWT "key"
     * query parameter can be provided as-is or in base64 encoded format.
     */
    public static Map<String, Object> parseGripUri(String uri) throws UnsupportedEncodingException, MalformedURLException {
        Map<String, Object> out = new HashMap<String, Object>();
        URL url = new URL(uri);
        Map<String, List<String>> params = Utilities.splitQuery(url);
        String iss = "";
        List<String> issQueryValue = params.get("iss");
        if (issQueryValue != null) {
            iss = issQueryValue.get(0);
            params.remove("iss");
        }
        String key = "";
        List<String> keyQueryValue = params.get("key");
        if (keyQueryValue != null) {
            key = keyQueryValue.get(0);
            params.remove("key");
        }
        if (key != null && key.startsWith("base64:"))
            key = new String(DatatypeConverter.parseBase64Binary(key.substring(7)));
        String queryString = "";
	    for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            if (queryString != "")
                queryString = queryString + "&";
            queryString = queryString + URLEncoder.encode(entry.getKey(), "UTF-8") +
                    "=" + URLEncoder.encode(entry.getValue().get(0), "UTF-8");
        }
        String path = url.getPath();
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        String port = "";
        if (url.getPort() > -1 && url.getPort() != 80)
            port = ":" + Integer.toString(url.getPort());
        String controlUri = url.getProtocol() + "://" + url.getHost() + port + path;
        if (queryString != "")
            controlUri = controlUri + "?" + queryString;
        out.put("control_uri", controlUri);
        if (iss != "")
            out.put("control_iss", iss);
        if (key != "")
            out.put("key", key);
        return out;
    }

    /**
     * Validate the specified JWT token and key.
     * This method is used to validate the GRIP-SIG header coming from GRIP
     * proxies such as Pushpin or Fanout.io. Note that the token expiration
     * is also verified.
     */
    public static boolean validateSig(String token, String key) {
        try {
            Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(key))
                    .parseClaimsJws(token).getBody();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    /**
     * Encode the specified array of WebSocketEvent instances.
     * The returned string value should then be passed to a GRIP proxy in the
     * body of an HTTP response when using the WebSocket-over-HTTP protocol.
     */
    public static String encodeWebSocketEvents(List<WebSocketEvent> webSocketEvents) {
        String out = "";
        for (WebSocketEvent event : webSocketEvents) {
            out = out + event.type;
            if (event.content != null)
                out = out + " " + Integer.toString(event.content.length(), 16) + "\r\n" + event.content + "\r\n";
            else {
                out = out + "\r\n";
            }
        }
        return out;
    }

    /**
     * Decode the request body into an array of WebSocketEvent instances.
     * A RuntimeError is raised if the format is invalid.
     */
    public static List<WebSocketEvent> decodeWebSocketEvents(String body) {
        List<WebSocketEvent> events = new ArrayList<>();
        int stringOffset = 0;
        int byteOffset = 0;
        byte[] bytes = body.getBytes();
        while (stringOffset < body.length()) {
            int at = body.indexOf("\r\n", stringOffset);
            if (at == -1)
                throw new IllegalArgumentException("bad format");
            String typeline = body.substring(stringOffset, at);
            stringOffset = at + 2;
            byteOffset += typeline.length() + 2;
            at = typeline.indexOf(" ");
            WebSocketEvent event;
            if (at >= 0) {
                String etype = typeline.substring(0, at);
                int clen = Integer.parseInt(typeline.substring(at + 1), 16);
                String content = new String(bytes, byteOffset, clen);
                stringOffset += content.length() + 2;
                byteOffset += clen + 2;
                event = new WebSocketEvent(etype, content);
            } else {
                event = new WebSocketEvent(typeline);
            }
            events.add(event);
        }
        return events;
    }

    /**
     * Decode the request body into an array of WebSocketEvent instances.
     * A RuntimeError is raised if the format is invalid.
     */
    public static List<WebSocketEvent> decodeWebSocketEvents(byte[] bytes) {
        return null;
    }

    /**
     * Get an array of hashes representing the specified channels parameter.
     * The resulting array is used for creating GRIP proxy hold instructions.
     */
    private static List<Map<String, Object>> getHoldChannels(List<Channel> channels) {
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
    private static Map<String, Object> getHoldResponse(Response response) {
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
}
