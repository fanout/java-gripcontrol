//    WebSocketEvent.java
//    ~~~~~~~~~
//    This module implements the WebSocketEvent class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.gripcontrol;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Event information used with the GRIP WebSocket-over-HTTP protocol.
 * Includes information about the type of event as well as an optional content field.
 */
public class WebSocketEvent {
    public String type;

    public final String content;
    public final byte[] contentBytes;

    /**
     * Initialize with a specified event type.
     */
    public WebSocketEvent(String type) {
        this.type = type;
        this.content = null;
        this.contentBytes = null;
    }

    /**
     * Initialize with a specified event type and content information.
     */
    public WebSocketEvent(String type, String content) {
        this.type = type;
        this.content = content;
        this.contentBytes = content.getBytes();
    }

    public WebSocketEvent(String type, byte[] contentBytes) {
        this.type = type;

        String asString;
        try {
            ByteBuffer buffer = ByteBuffer.wrap(contentBytes);
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            CharBuffer charBuffer = decoder.decode(buffer);
            asString = charBuffer.toString();
        } catch(CharacterCodingException ex) {
            asString = null;
        }

        this.content = asString;
        this.contentBytes = contentBytes;
    }
}
