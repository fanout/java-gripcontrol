package org.fanout.gripcontrol;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class UtilitiesTest {
    private static final byte[][] byteTests = {
        {(byte) Integer.parseInt("11001111", 2), (byte) Integer.parseInt("10111111", 2)},
        {(byte) Integer.parseInt("11101111", 2), (byte) Integer.parseInt("10101010", 2), (byte) Integer.parseInt("10111111", 2)},
        {(byte) Integer.parseInt("10001111", 2), (byte) Integer.parseInt("10111111", 2)},
        {(byte) Integer.parseInt("11101111", 2), (byte) Integer.parseInt("10101010", 2), (byte) Integer.parseInt("00111111", 2)}
    };

    private static final boolean[] byteTestsExpectedResults = {
        true,
        true,
        false,
        false
    };

    private static final String[] stringTests = {
        "A",
        "Z",
        "A valid UTF8 string",
        "emojis 😀😃😄😁😆😅🤣😂😍☼✂💖⛺",
    };

    @Test
    public void testDecodeUtf8String() {

        assertEquals( Utilities.utf8BytesToString( new byte[] { 0x48, 0x65, 0x6c, 0x6c, 0x6f, } ), "Hello" );
        assertEquals( Utilities.utf8BytesToString( new byte[] { (byte) 0xc3, (byte) 0xb1, } ), "ñ" );
        assertNull(Utilities.utf8BytesToString(new byte[]{(byte) 0xc3, (byte) 0x28,}));

    }

    @Test
    public void testValidate() {
        for (int i = 0; i < byteTests.length; i++) {
            assertEquals(String.format("validate(byteTests[%d])", i),
                    Utilities.isUtf8(byteTests[i]),
                    byteTestsExpectedResults[i]);
        }
    }

    @Test
    public void testValidateFromString() throws UnsupportedEncodingException {
        for (String toTest : stringTests) {
            assertTrue(String.format("validate('%s')", toTest),
                    Utilities.isUtf8(toTest.getBytes("utf-8")));
        }
    }

    @Test
    public void testCharLengthFromString() throws UnsupportedEncodingException {
        for (String toTest : stringTests) {
            assertEquals(String.format("charLength('%s')", toTest),
                    Utilities.charLength(toTest.getBytes("utf-8")),
                    toTest.length());
        }
    }
}
