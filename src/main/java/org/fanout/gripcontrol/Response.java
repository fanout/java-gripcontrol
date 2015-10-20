//    Response.java
//    ~~~~~~~~~
//    This module implements the Response class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.gripcontrol;

import java.util.*;

/**
 * The Response class is used to represent a set of HTTP response data.
 * Populated instances of this class are serialized to JSON and passed
 * to the GRIP proxy in the body. The GRIP proxy then parses the message
 * and deserialized the JSON into an HTTP response that is passed back
 * to the client.
 */
public class Response {
    public String body;
    public Map<String, String>  headers;
    public String code;
    public String reason;

    /**
     * Initialize with the body.
     */
    public Response(String body) {
        this.body = body;
    }

    /**
     * Initialize with the body and headers.
     */
    public Response(String body, Map<String, String> headers) {
        this.body = body;
        this.headers = headers;
    }

    /**
     * Initialize with the body, headers, code and reason.
     */
    public Response(String body, Map<String, String> headers, String code, String reason) {
        this.body = body;
        this.headers = headers;
        this.code = code;
        this.reason = reason;
    }
}
