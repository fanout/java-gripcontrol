//    Utilities.java
//    ~~~~~~~~~
//    This module implements the Utilities class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.gripcontrol;

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
}
