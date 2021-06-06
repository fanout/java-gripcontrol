//    Utilities.java
//    ~~~~~~~~~
//    This module implements the Utilities class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.gripcontrol;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.*;
import java.io.UnsupportedEncodingException;

/**
 * Static utilities used with the GRIP features.
 */
public class Utilities {

    public static String utf8BytesToString(byte[] bytes) {
        String asString;
        try {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            CharBuffer charBuffer = decoder.decode(buffer);
            asString = charBuffer.toString();
        } catch(CharacterCodingException ex) {
            asString = null;
        }

        return asString;
    }

    /**
     * Returns the number of UTF-8 characters.
     */
    public static int charLength(byte[] bytes) {
        String asString = utf8BytesToString(bytes);

        if (asString == null) {
            return -1;
        }

        return asString.length();
    }

    /**
     * Validate a UTF-8 byte array.
     */
    public static boolean isUtf8(byte[] bytes) {
        return utf8BytesToString(bytes) != null;
    }

    /**
     * Split the query string in the specified URL.
     */
    public static Map<String, List<String>> splitQuery(URL url) throws UnsupportedEncodingException {
        final Map<String, List<String>> queryPairs = new LinkedHashMap<String, List<String>>();
        String query = url.getQuery();
        if (query == null)
            return queryPairs;
        final String[] pairs = query.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!queryPairs.containsKey(key)) {
                queryPairs.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            queryPairs.get(key).add(value);
        }
        return queryPairs;
    }
}
