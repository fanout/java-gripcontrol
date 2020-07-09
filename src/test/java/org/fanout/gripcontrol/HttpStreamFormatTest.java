package org.fanout.gripcontrol;

import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class HttpStreamFormatTest {
    @Test
    public void testHttpStreamFormat() throws UnsupportedEncodingException, IllegalArgumentException {
        HttpStreamFormat format = new HttpStreamFormat("content");
        assertEquals(format.name(), "http-stream");
        Map<String, Object> export = (Map<String, Object>)format.export();
        assertEquals(export.get("content"), "content");
        format = new HttpStreamFormat("content".getBytes("utf-8"));
        export = (Map<String, Object>)format.export();
        assertEquals(export.get("content"), "content");
        byte[] byteData = {(byte) Integer.parseInt("10001111", 2), (byte) Integer.parseInt("10111111", 2)};
        format = new HttpStreamFormat(byteData);
        export = (Map<String, Object>)format.export();
        assertEquals(export.get("content-bin"), DatatypeConverter.printBase64Binary(byteData));
        format = new HttpStreamFormat(HttpStreamAction.CLOSE);
        export = (Map<String, Object>)format.export();
        assertEquals(export.get("action"), "close");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testHttpStreamFormatException1() throws UnsupportedEncodingException, IllegalArgumentException {
        HttpStreamFormat format = new HttpStreamFormat((byte[])null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testHttpStreamFormatException2() throws UnsupportedEncodingException, IllegalArgumentException {
        HttpStreamFormat format = new HttpStreamFormat("");
    }
}
