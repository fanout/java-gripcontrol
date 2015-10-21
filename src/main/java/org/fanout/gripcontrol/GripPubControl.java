//    GripPubControl.java
//    ~~~~~~~~~
//    This module implements the GripPubControl class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.gripcontrol;

import java.util.*;
import org.fanout.pubcontrol.*;
import java.io.UnsupportedEncodingException;

/**
 * Allows consumers to publish HTTP format messages to GRIP proxies.
 * Configuring GripPubControl is slightly different from configuring
 * PubControl in that the 'uri' and 'iss' keys in each config entry
 * should have a 'control_' prefix. GripPubControl inherits from PubControl
 * and therefore also provides all of the same functionality.
 */
public class GripPubControl extends PubControl {
    /**
     * Initialize with or without a configuration.
     * A configuration can be applied after initialization via the applyGripConfig method.
     */
    public GripPubControl()
    {
        super(null);
    }

    /**
     * Initialize with a configuration.
     */
    public GripPubControl(List<Map<String, Object>> config) {
        super(null);
        if (config != null)
            applyGripConfig(config);
    }

    /**
     * Apply the specified configuration to this GripPubControl instance.
     * The configuration object can either be a hash or an array of hashes where
     * each hash corresponds to a single PubControlClient instance. Each hash
     * will be parsed and a PubControlClient will be created either using just
     * a URI or a URI and JWT authentication information.
     */
    @SuppressWarnings({"unchecked"})
    public void applyGripConfig(List<Map<String, Object>> config) {
       for (Map<String, Object> entry : config) {
           String uri = null;
           if (entry.get("control_uri") != null)
               uri = (String)entry.get("control_uri");
           PubControlClient client = new PubControlClient(uri);
           Object iss = entry.get("control_iss");
           Object key = entry.get("key");
           if (iss != null && key != null) {
               Map<String, Object> claims = new HashMap<String, Object>();
               claims.put("iss", (String)iss);
               client.setAuthJwt(claims, (byte[])key);
           }
           super.addClient(client);
       }
    }

    /**
     * Synchronously publish an HTTP response format message
     */
    public void publishHttpResponse(List<String> channels, HttpResponseFormat format,
            String id, String prevId) throws PublishFailedException {
        List<Format> formats = new ArrayList<Format>();
        formats.add(format);
        Item item = new Item(formats, id, prevId);
        super.publish(channels, item);
    }

    /**
     * Synchronously publish an HTTP response format message.
     */
    public void publishHttpResponse(List<String> channels, String body,
            String id, String prevId)
            throws PublishFailedException, UnsupportedEncodingException {
        List<Format> formats = new ArrayList<Format>();
        HttpResponseFormat format = new HttpResponseFormat(body);
        formats.add(format);
        Item item = new Item(formats, id, prevId);
        super.publish(channels, item);
    }

    /**
     * Synchronously publish an HTTP response format message.
     */
    public void publishHttpResponse(List<String> channels, byte[] body,
            String id, String prevId) throws PublishFailedException {
        List<Format> formats = new ArrayList<Format>();
        HttpResponseFormat format = new HttpResponseFormat(body);
        formats.add(format);
        Item item = new Item(formats, id, prevId);
        super.publish(channels, item);
    }

    /**
     * Synchronously publish an HTTP response format message.
     */
    public void publishHttpResponse(List<String> channels, HttpResponseFormat format)
            throws PublishFailedException {
        List<Format> formats = new ArrayList<Format>();
        formats.add(format);
        Item item = new Item(formats, null, null);
        super.publish(channels, item);
    }

    /**
     * Synchronously publish an HTTP response format message.
     */
    public void publishHttpResponse(List<String> channels, String body)
            throws PublishFailedException, UnsupportedEncodingException {
        List<Format> formats = new ArrayList<Format>();
        HttpResponseFormat format = new HttpResponseFormat(body);
        formats.add(format);
        Item item = new Item(formats, null, null);
        super.publish(channels, item);
    }

    /**
     * Synchronously publish an HTTP response format message.
     */
    public void publishHttpResponse(List<String> channels, byte[] body) throws PublishFailedException {
        List<Format> formats = new ArrayList<Format>();
        HttpResponseFormat format = new HttpResponseFormat(body);
        formats.add(format);
        Item item = new Item(formats, null, null);
        super.publish(channels, item);
    }

    /**
     * Asynchronously publish an HTTP response format message.
     */
    public void publishHttpResponseAsync(List<String> channels, HttpResponseFormat format,
            String id, String prevId, PublishCallback callback) {
        List<Format> formats = new ArrayList<Format>();
        formats.add(format);
        Item item = new Item(formats, id, prevId);
        super.publishAsync(channels, item, callback);
    }

    /**
     * Asynchronously publish an HTTP response format message.
     */
    public void publishHttpResponseAsync(List<String> channels, String body,
            String id, String prevId, PublishCallback callback)
            throws UnsupportedEncodingException {
        List<Format> formats = new ArrayList<Format>();
        HttpResponseFormat format = new HttpResponseFormat(body);
        formats.add(format);
        Item item = new Item(formats, id, prevId);
        super.publishAsync(channels, item, callback);
    }

    /**
     * Asynchronously publish an HTTP response format message.
     */
    public void publishHttpResponseAsync(List<String> channels, byte[] body,
            String id, String prevId, PublishCallback callback) {
        List<Format> formats = new ArrayList<Format>();
        HttpResponseFormat format = new HttpResponseFormat(body);
        formats.add(format);
        Item item = new Item(formats, id, prevId);
        super.publishAsync(channels, item, callback);
    }

    /**
     * Asynchronously publish an HTTP response format message.
     */
    public void publishHttpResponseAsync(List<String> channels, HttpResponseFormat format,
            PublishCallback callback) {
        List<Format> formats = new ArrayList<Format>();
        formats.add(format);
        Item item = new Item(formats, null, null);
        super.publishAsync(channels, item, callback);
    }

    /**
     * Asynchronously publish an HTTP response format message.
     */
    public void publishHttpResponseAsync(List<String> channels, String body, PublishCallback callback)
            throws UnsupportedEncodingException {
        List<Format> formats = new ArrayList<Format>();
        HttpResponseFormat format = new HttpResponseFormat(body);
        formats.add(format);
        Item item = new Item(formats, null, null);
        super.publishAsync(channels, item, callback);
    }

    /**
     * Asynchronously publish an HTTP response format message.
     */
    public void publishHttpResponseAsync(List<String> channels, byte[] body,
            PublishCallback callback) {
        List<Format> formats = new ArrayList<Format>();
        HttpResponseFormat format = new HttpResponseFormat(body);
        formats.add(format);
        Item item = new Item(formats, null, null);
        super.publishAsync(channels, item, callback);
    }

    /**
     * Synchronously publish an HTTP stream format message.
     */
    public void publishHttpStream(List<String> channels, HttpStreamFormat format,
            String id, String prevId) throws PublishFailedException {
        List<Format> formats = new ArrayList<Format>();
        formats.add(format);
        Item item = new Item(formats, id, prevId);
        super.publish(channels, item);
    }

    /**
     * Synchronously publish an HTTP stream format message.
     */
    public void publishHttpStream(List<String> channels, String content,
            String id, String prevId)
            throws PublishFailedException, UnsupportedEncodingException {
        List<Format> formats = new ArrayList<Format>();
        HttpStreamFormat format = new HttpStreamFormat(content);
        formats.add(format);
        Item item = new Item(formats, id, prevId);
        super.publish(channels, item);
    }

    /**
     * Synchronously publish an HTTP stream format message.
     */
    public void publishHttpStream(List<String> channels, byte[] content,
            String id, String prevId) throws PublishFailedException {
        List<Format> formats = new ArrayList<Format>();
        HttpStreamFormat format = new HttpStreamFormat(content);
        formats.add(format);
        Item item = new Item(formats, id, prevId);
        super.publish(channels, item);
    }

    /**
     * Synchronously publish an HTTP stream format message.
     */
    public void publishHttpStream(List<String> channels, HttpStreamFormat format)
            throws PublishFailedException {
        List<Format> formats = new ArrayList<Format>();
        formats.add(format);
        Item item = new Item(formats, null, null);
        super.publish(channels, item);
    }

    /**
     * Synchronously publish an HTTP stream format message.
     */
    public void publishHttpStream(List<String> channels, String content)
            throws PublishFailedException, UnsupportedEncodingException {
        List<Format> formats = new ArrayList<Format>();
        HttpStreamFormat format = new HttpStreamFormat(content);
        formats.add(format);
        Item item = new Item(formats, null, null);
        super.publish(channels, item);
    }

    /**
     * Synchronously publish an HTTP stream format message.
     */
    public void publishHttpStream(List<String> channels, byte[] content) throws PublishFailedException {
        List<Format> formats = new ArrayList<Format>();
        HttpStreamFormat format = new HttpStreamFormat(content);
        formats.add(format);
        Item item = new Item(formats, null, null);
        super.publish(channels, item);
    }

    /**
     * Asynchronously publish an HTTP stream format message.
     */
    public void publishHttpStreamAsync(List<String> channels, HttpStreamFormat format,
            String id, String prevId, PublishCallback callback) {
        List<Format> formats = new ArrayList<Format>();
        formats.add(format);
        Item item = new Item(formats, id, prevId);
        super.publishAsync(channels, item, callback);
    }

    /**
     * Asynchronously publish an HTTP stream format message.
     */
    public void publishHttpStreamAsync(List<String> channels, String content,
            String id, String prevId, PublishCallback callback)
            throws UnsupportedEncodingException {
        List<Format> formats = new ArrayList<Format>();
        HttpStreamFormat format = new HttpStreamFormat(content);
        formats.add(format);
        Item item = new Item(formats, id, prevId);
        super.publishAsync(channels, item, callback);
    }

    /**
     * Asynchronously publish an HTTP stream format message.
     */
    public void publishHttpStreamAsync(List<String> channels, byte[] content,
            String id, String prevId, PublishCallback callback) {
        List<Format> formats = new ArrayList<Format>();
        HttpStreamFormat format = new HttpStreamFormat(content);
        formats.add(format);
        Item item = new Item(formats, id, prevId);
        super.publishAsync(channels, item, callback);
    }

    /**
     * Asynchronously publish an HTTP stream format message.
     */
    public void publishHttpStreamAsync(List<String> channels, HttpStreamFormat format,
            PublishCallback callback) {
        List<Format> formats = new ArrayList<Format>();
        formats.add(format);
        Item item = new Item(formats, null, null);
        super.publishAsync(channels, item, callback);
    }

    /**
     * Asynchronously publish an HTTP stream format message.
     */
    public void publishHttpStreamAsync(List<String> channels, String content, PublishCallback callback)
            throws UnsupportedEncodingException {
        List<Format> formats = new ArrayList<Format>();
        HttpStreamFormat format = new HttpStreamFormat(content);
        formats.add(format);
        Item item = new Item(formats, null, null);
        super.publishAsync(channels, item, callback);
    }

    /**
     * Asynchronously publish an HTTP stream format message.
     */
    public void publishHttpStreamAsync(List<String> channels, byte[] content,
            PublishCallback callback) {
        List<Format> formats = new ArrayList<Format>();
        HttpStreamFormat format = new HttpStreamFormat(content);
        formats.add(format);
        Item item = new Item(formats, null, null);
        super.publishAsync(channels, item, callback);
    }
}
