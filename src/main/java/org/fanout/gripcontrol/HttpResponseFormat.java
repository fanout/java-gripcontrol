//    HttpResponseFormat.java
//    ~~~~~~~~~
//    This module implements the HttpResponseFormat class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.gripcontrol;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import org.fanout.pubcontrol.Format;
import java.util.*;

/**
 * Used to publish messages to HTTP response clients connected to a GRIP proxy.
 */
public class HttpResponseFormat implements Format {
    public byte[] body;
    public Map<String, String>  headers;
    public String code;
    public String reason;

    /**
     * Initialize with the body.
     */
    public HttpResponseFormat(String body) throws UnsupportedEncodingException {
        this.body = body.getBytes("utf-8");
    }

    /**
     * Initialize with the body, headers, code and reason.
     */
    public HttpResponseFormat(String body, Map<String, String> headers, String code,
            String reason) throws UnsupportedEncodingException {
        this.body = body.getBytes("utf-8");
        this.headers = headers;
        this.code = code;
        this.reason = reason;
    }

    /**
     * Initialize with the body.
     */
    public HttpResponseFormat(byte[] body) {
        this.body = body;
    }

    /**
     * Initialize with the body, headers, code and reason.
     */
    public HttpResponseFormat(byte[] body, Map<String, String> headers, String code, String reason) {
        this.body = body;
        this.headers = headers;
        this.code = code;
        this.reason = reason;
    }

    /**
     * The name used when publishing this format.
     */
    public String name() {
        return "http-response";
    }

    /**
     * Export the message into the required format.
     * Include only the fields that are set. The body is exported as base64
     * if the byte array is binary.
     */
    public Object export() {
        Map<String, Object> export = new HashMap<String, Object>();
        if (this.code != null)
            export.put("code", this.code);
        if (this.reason != null)
            export.put("reason", this.reason);
        if (this.headers != null)
            export.put("headers", this.headers);
        if (this.body != null) {
            if (Utilities.isUtf8(this.body)) {
                try {
                    export.put("body", new String(this.body, "utf-8"));
                } catch (UnsupportedEncodingException e) { }
            } else {
                export.put("body-bin", DatatypeConverter.printBase64Binary(this.body));
            }
        }
        return export;
    }
}
