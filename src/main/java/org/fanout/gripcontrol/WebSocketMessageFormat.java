//    WebSocketMessageFormat.java
//    ~~~~~~~~~
//    This module implements the WebSocketMessageFormat class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.gripcontrol;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import org.fanout.pubcontrol.Format;
import java.util.*;

/**
 * Used to publish data to WebSocket clients connected to GRIP proxies.
 */
public class WebSocketMessageFormat implements Format {
    public String content;
    public byte[] binaryContent;

    /**
     * Initialize with non-binary string content.
     */
    public WebSocketMessageFormat(String content) {
        this.content = content;
    }

    /**
     * Initialize with binary content.
     */
    public WebSocketMessageFormat(byte[] binaryContent) {
        this.binaryContent = binaryContent;
    }

    /**
     * The name used when publishing this format.
     */
    public String name() {
        return "ws-message";
    }

    /**
     * Exports the message based on whether the message content is binary or not.
     */
    public Object export() {
        Map<String, Object> export = new HashMap<String, Object>();
        if (this.binaryContent != null)
            export.put("content-bin", DatatypeConverter.printBase64Binary(this.binaryContent));
        else if (this.content != null)
            export.put("content", this.content);
        return export;
    }
}
