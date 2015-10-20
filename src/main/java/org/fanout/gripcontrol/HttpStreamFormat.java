//    HttpStreamFormat.java
//    ~~~~~~~~~
//    This module implements the HttpStreamFormat class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.gripcontrol;

import java.lang.IllegalArgumentException;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import org.fanout.pubcontrol.Format;
import java.util.*;

/**
 * Used to publish messages to HTTP stream clients connected to a GRIP proxy.
 */
public class HttpStreamFormat implements Format {
    public byte[] content;
    public Boolean isClose = false;

    /**
     * Initialize with an action.
     */
    public HttpStreamFormat(HttpStreamAction action) throws
            UnsupportedEncodingException, IllegalArgumentException {
        if (action == HttpStreamAction.CLOSE)
            this.isClose = true;
    }

    /**
     * Initialize with string content.
     */
    public HttpStreamFormat(String content) throws
            UnsupportedEncodingException, IllegalArgumentException {
        this.content = content.getBytes("utf-8");
        verifyContent();
    }

    /**
     * Initialize with byte array content.
     */
    public HttpStreamFormat(byte[] content) throws
            UnsupportedEncodingException, IllegalArgumentException {
        this.content = content;
        verifyContent();
    }

    /**
    /**
     * The name used when publishing this format.
     */
    public String name() {
        return "http-stream";
    }

    /**
     * Export the message into the required format.
     * Include only the fields that are set. The body is exported as base64
     * if the byte array is binary.
     */
    public Object export() {
        Map<String, Object> export = new HashMap<String, Object>();
        if (this.isClose) {
            export.put("action", "close");
        } else {
            if (Utilities.isUtf8(this.content)) {
                try {
                    export.put("content", new String(this.content, "utf-8"));
                } catch (UnsupportedEncodingException e) { }
            } else {
                export.put("content-bin", DatatypeConverter.printBase64Binary(this.content));
            }
        }
        return export;
    }

    /**
     * Verify the content by ensuring that is it present if close status is True.
     */
    private void verifyContent() throws IllegalArgumentException {
        if (!this.isClose && (this.content == null || this.content.length == 0))
            throw new IllegalArgumentException("Content must be set");
    }
}
