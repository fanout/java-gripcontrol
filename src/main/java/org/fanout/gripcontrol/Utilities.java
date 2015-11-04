//    Utilities.java
//    ~~~~~~~~~
//    This module implements the Utilities class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.gripcontrol;

import java.util.*;
import java.net.*;
import java.io.UnsupportedEncodingException;

/**
 * Static utilities used with the GRIP features.
 */
public class Utilities {
    /**
     * Returns the number of UTF-8 characters.
     */
    public static int charLength(byte[] bytes) {
        int charCount = 0, expectedLen;

        for (int i = 0; i < bytes.length; i++) {
            charCount++;
            if ((bytes[i] & Integer.parseInt("10000000", 2)) == Integer.parseInt("00000000", 2)) {
                continue;
            } else if ((bytes[i] & Integer.parseInt("11100000", 2)) == Integer.parseInt("11000000", 2)) {
                expectedLen = 2;
            } else if ((bytes[i] & Integer.parseInt("11110000", 2)) == Integer.parseInt("11100000", 2)) {
                expectedLen = 3;
            } else if ((bytes[i] & Integer.parseInt("11111000", 2)) == Integer.parseInt("11110000", 2)) {
                expectedLen = 4;
            } else if ((bytes[i] & Integer.parseInt("11111100", 2)) == Integer.parseInt("11111000", 2)) {
                expectedLen = 5;
            } else if ((bytes[i] & Integer.parseInt("11111110", 2)) == Integer.parseInt("11111100", 2)) {
                expectedLen = 6;
            } else {
                return -1;
            }

            while (--expectedLen > 0) {
                if (++i >= bytes.length) {
                    return -1;
                }
                if ((bytes[i] & Integer.parseInt("11000000", 2)) != Integer.parseInt("10000000", 2)) {
                    return -1;
                }
            }
        }
        return charCount;
    }

    /**
     * Validate a UTF-8 byte array.
     */
    public static boolean isUtf8(byte[] bytes) {
        return (charLength(bytes) != -1);
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
