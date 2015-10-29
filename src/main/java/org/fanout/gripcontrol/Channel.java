//    Channel.java
//    ~~~~~~~~~
//    This module implements the Channel class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.gripcontrol;

/**
 * The Channel class is used to represent a channel in for a GRIP proxy.
 */
public class Channel {
    public String name;
    public String prevId;

    /**
     * Initialize with the channel name.
     */
    public Channel(String name) {
        this.name = name;
    }

    /**
     * Initialize with the channel name and an optional previous ID.
     */
    public Channel(String name, String prevId) {
        this.name = name;
        this.prevId = prevId;
    }
}
