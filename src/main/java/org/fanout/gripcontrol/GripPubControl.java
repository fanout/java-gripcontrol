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
           } else if (key != null) {
               client.setAuthBearer((String)key);
           }
           super.addClient(client);
       }
    }

    /**
     * Synchronously publish an HTTP response format message
     */
    public void publishHttpResponse(List<String> channels, HttpResponseFormat format,
            String id, String prevId) throws PublishFailedException {
        super.publish(channels, new Item(getListOfFormats(format), id, prevId));
    }

    /**
     * Synchronously publish an HTTP response format message.
     */
    public void publishHttpResponse(List<String> channels, String body,
            String id, String prevId)
            throws PublishFailedException, UnsupportedEncodingException {
        super.publish(channels, new Item(getListOfFormats(new HttpResponseFormat(body)), id, prevId));
    }

    /**
     * Synchronously publish an HTTP response format message.
     */
    public void publishHttpResponse(List<String> channels, byte[] body,
            String id, String prevId) throws PublishFailedException {
        super.publish(channels, new Item(getListOfFormats(new HttpResponseFormat(body)), id, prevId));
    }

    /**
     * Synchronously publish an HTTP response format message.
     */
    public void publishHttpResponse(List<String> channels, HttpResponseFormat format)
            throws PublishFailedException {
        super.publish(channels, new Item(getListOfFormats(format), null, null));
    }

    /**
     * Synchronously publish an HTTP response format message.
     */
    public void publishHttpResponse(List<String> channels, String body)
            throws PublishFailedException, UnsupportedEncodingException {
        super.publish(channels, new Item(getListOfFormats(new HttpResponseFormat(body)), null, null));
    }

    /**
     * Synchronously publish an HTTP response format message.
     */
    public void publishHttpResponse(List<String> channels, byte[] body) throws PublishFailedException {
        super.publish(channels, new Item(getListOfFormats(new HttpResponseFormat(body)), null, null));
    }

    /**
     * Asynchronously publish an HTTP response format message.
     */
    public void publishHttpResponseAsync(List<String> channels, HttpResponseFormat format,
            String id, String prevId, PublishCallback callback) {
        super.publishAsync(channels, new Item(getListOfFormats(format), id, prevId), callback);
    }

    /**
     * Asynchronously publish an HTTP response format message.
     */
    public void publishHttpResponseAsync(List<String> channels, String body,
            String id, String prevId, PublishCallback callback)
            throws UnsupportedEncodingException {
        super.publishAsync(channels,
                new Item(getListOfFormats(new HttpResponseFormat(body)), id, prevId), callback);
    }

    /**
     * Asynchronously publish an HTTP response format message.
     */
    public void publishHttpResponseAsync(List<String> channels, byte[] body,
            String id, String prevId, PublishCallback callback) {
        super.publishAsync(channels,
                new Item(getListOfFormats(new HttpResponseFormat(body)), id, prevId), callback);
    }

    /**
     * Asynchronously publish an HTTP response format message.
     */
    public void publishHttpResponseAsync(List<String> channels, HttpResponseFormat format,
            PublishCallback callback) {
        super.publishAsync(channels,
                new Item(getListOfFormats(format), null, null), callback);
    }

    /**
     * Asynchronously publish an HTTP response format message.
     */
    public void publishHttpResponseAsync(List<String> channels, String body, PublishCallback callback)
            throws UnsupportedEncodingException {
        super.publishAsync(channels,
                new Item(getListOfFormats(new HttpResponseFormat(body)), null, null), callback);
    }

    /**
     * Asynchronously publish an HTTP response format message.
     */
    public void publishHttpResponseAsync(List<String> channels, byte[] body,
            PublishCallback callback) {
        super.publishAsync(channels,
                new Item(getListOfFormats(new HttpResponseFormat(body)), null, null), callback);
    }

    /**
     * Synchronously publish an HTTP stream format message.
     */
    public void publishHttpStream(List<String> channels, HttpStreamFormat format,
            String id, String prevId) throws PublishFailedException {
        super.publish(channels, new Item(getListOfFormats(format), id, prevId));
    }

    /**
     * Synchronously publish an HTTP stream format message.
     */
    public void publishHttpStream(List<String> channels, String content,
            String id, String prevId)
            throws PublishFailedException, UnsupportedEncodingException {
        super.publish(channels, new Item(getListOfFormats(new HttpStreamFormat(content)), id, prevId));
    }

    /**
     * Synchronously publish an HTTP stream format message.
     */
    public void publishHttpStream(List<String> channels, byte[] content,
            String id, String prevId) throws PublishFailedException {
        super.publish(channels, new Item(getListOfFormats(new HttpStreamFormat(content)), id, prevId));
    }

    /**
     * Synchronously publish an HTTP stream format message.
     */
    public void publishHttpStream(List<String> channels, HttpStreamFormat format)
            throws PublishFailedException {
        super.publish(channels, new Item(getListOfFormats(format), null, null));
    }

    /**
     * Synchronously publish an HTTP stream format message.
     */
    public void publishHttpStream(List<String> channels, String content)
            throws PublishFailedException, UnsupportedEncodingException {
        super.publish(channels, new Item(getListOfFormats(new HttpStreamFormat(content)), null, null));
    }

    /**
     * Synchronously publish an HTTP stream format message.
     */
    public void publishHttpStream(List<String> channels, byte[] content) throws PublishFailedException {
        super.publish(channels, new Item(getListOfFormats(new HttpStreamFormat(content)), null, null));
    }

    /**
     * Asynchronously publish an HTTP stream format message.
     */
    public void publishHttpStreamAsync(List<String> channels, HttpStreamFormat format,
            String id, String prevId, PublishCallback callback) {
        super.publishAsync(channels, new Item(getListOfFormats(format), id, prevId), callback);
    }

    /**
     * Asynchronously publish an HTTP stream format message.
     */
    public void publishHttpStreamAsync(List<String> channels, String content,
            String id, String prevId, PublishCallback callback)
            throws UnsupportedEncodingException {
        super.publishAsync(channels,
                new Item(getListOfFormats(new HttpStreamFormat(content)), id, prevId), callback);
    }

    /**
     * Asynchronously publish an HTTP stream format message.
     */
    public void publishHttpStreamAsync(List<String> channels, byte[] content,
            String id, String prevId, PublishCallback callback) {
        super.publishAsync(channels,
                new Item(getListOfFormats(new HttpStreamFormat(content)), id, prevId), callback);
    }

    /**
     * Asynchronously publish an HTTP stream format message.
     */
    public void publishHttpStreamAsync(List<String> channels, HttpStreamFormat format,
            PublishCallback callback) {
        super.publishAsync(channels, new Item(getListOfFormats(format), null, null), callback);
    }

    /**
     * Asynchronously publish an HTTP stream format message.
     */
    public void publishHttpStreamAsync(List<String> channels, String content, PublishCallback callback)
            throws UnsupportedEncodingException {
        super.publishAsync(channels,
                new Item(getListOfFormats(new HttpStreamFormat(content)), null, null), callback);
    }

    /**
     * Asynchronously publish an HTTP stream format message.
     */
    public void publishHttpStreamAsync(List<String> channels, byte[] content,
            PublishCallback callback) {
        super.publishAsync(channels,
                new Item(getListOfFormats(new HttpStreamFormat(content)), null, null), callback);
    }

    /**
     * Get a list of formats containing the specified format.
     */
    private List<Format> getListOfFormats(Format format) {
        List<Format> formats = new ArrayList<Format>();
        formats.add(format);
        return formats;
    }
}
