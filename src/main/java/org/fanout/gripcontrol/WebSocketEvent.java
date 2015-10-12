//    WebSocketEvent.rb
//    ~~~~~~~~~
//    This module implements the WebSocketEvent class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.gripcontrol;

/** Event information used with the GRIP WebSocket-over-HTTP protocol.
 * Includes information about the type of event as well as an optional content field.
 */
public class WebSocketEvent {
    public String type;
    public String content;

    /**
     * Initialize with a specified event type.
     */
    public WebSocketEvent(String type)
    {
        this.type = type;
    }

    /**
     * Initialize with a specified event type and content information.
     */
    public WebSocketEvent(String type, String content)
    {
        this.type = type;
        this.content = content;
    }
}
