java-gripcontrol
===============

Author: Konstantin Bokarius <kon@fanout.io>

A GRIP library for Java.

License
-------

java-gripcontrol is offered under the MIT license. See the LICENSE file.

Installation
------------

java-gripcontrol is compatible with JDK 6 and above.

Maven:

```xml
<dependency>
  <groupId>org.fanout</groupId>
  <artifactId>gripcontrol</artifactId>
  <version>1.2.1</version>
</dependency>
<dependency>
  <groupId>org.fanout</groupId>
  <artifactId>pubcontrol</artifactId>
  <version>1.0.9</version>
</dependency>
```

HTTPS Publishing
----------------

Note that on some operating systems Java may require you to add the root CA certificate of the publishing server to the key store. This is particularly the case with OSX. Follow the steps outlined in this article to address the issue: http://nodsw.com/blog/leeland/2006/12/06-no-more-unable-find-valid-certification-path-requested-target

Also, if using Java 6 you may run into SNI issues. If this occurs we recommend HTTP-only publishing or upgrading to Java 7 or above.

Usage
-----

Examples for how to publish HTTP response and HTTP stream messages to GRIP proxy endpoints via the GripPubControl class.

```java
import org.fanout.gripcontrol.*;
import org.fanout.pubcontrol.*;
import javax.xml.bind.DatatypeConverter;
import java.util.*;

public class GripPubControlExample {
    private static class Callback implements PublishCallback {
        public void completed(boolean result, String message) {
            if (result)
                System.out.println("Publish successful");
            else
                System.out.println("Publish failed with message: " + message);
        }
    }

    public static void main(String[] args) {
        // PubControl can be initialized with or without an endpoint configuration.
        // Each endpoint can include optional JWT authentication info.
        // Multiple endpoints can be included in a single configuration.

        // Initialize PubControl with a single endpoint:
        List<Map<String, Object>> config = new ArrayList<Map<String, Object>>();
        Map<String, Object> entry = new HashMap<String, Object>();
        entry.put("control_uri", "https://api.fastly.com/service/<service-id>");
        // The API token needs to have purge permission
        entry.put("key", "<fastly-api-token>");
        config.add(entry);
        GripPubControl pub = new GripPubControl(config);

        // Add new endpoints by applying an endpoint configuration:
        config = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> entry1 = new HashMap<String, Object>();
        entry1.put("control_uri", "<myendpoint_uri_1>");
        config.add(entry1);
        HashMap<String, Object> entry2 = new HashMap<String, Object>();
        entry2.put("control_uri", "<myendpoint_uri_2>");
        config.add(entry2);
        pub.applyConfig(config);

        // Remove all configured endpoints:
        pub.removeAllClients();

        // Explicitly add an endpoint as a PubControlClient instance:
        PubControlClient pubClient = new PubControlClient("<myendpoint_uri");
        // Optionally set JWT auth: pubClient.setAuthJwt(<claims>, '<key>')
        // Optionally set basic auth: pubClient.setAuthBasic('<user>', '<password>')
        pub.addClient(pubClient);

        // Publish across all configured endpoints:
        List<String> channels = new ArrayList<String>();
        channels.add("<channel>");
        try {
            pub.publishHttpResponse(channels, "Test publish!");
            pub.publishHttpStream(channels, "Test publish!");
        } catch (PublishFailedException exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
        pub.publishHttpResponseAsync(channels, "Test publish!", new Callback());
        pub.publishHttpStreamAsync(channels, "Test publish!", new Callback());

        // Wait for all async publish calls to complete:
        pub.finish();
    }
}
```

Validate the Grip-Sig request header from incoming GRIP messages. This ensures that the message was sent from a valid source and is not expired. When using Pushpin the key is configurable in Pushpin's settings. The key is passed in base64 encoded format (Fastly Fanout support is forthcoming).

```java
boolean isValid = GripControl.validateSig(headers["Grip-Sig"], "<key>");
```

Long polling example via response _headers_ using the NanoHTTPD web server. The client connects to a GRIP proxy over HTTP and the proxy forwards the request to the origin. The origin subscribes the client to a channel and instructs it to long poll via the response _headers_. Note that with the recent versions of Apache it's not possible to send a 304 response containing custom headers, in which case the response body should be used instead (next usage example below).

```java
package com.example;

import java.util.*;
import java.io.IOException;
import fi.iki.elonen.NanoHTTPD;
import org.fanout.gripcontrol.*;
import org.fanout.pubcontrol.*;

public class App extends NanoHTTPD {

    public App() throws IOException {
        super(80);
        start();
    }

    public static void main(String[] args) {
        try {
            new App();
        }
        catch (IOException exception) { }
    }

    @Override
    public Response serve(IHTTPSession session) {
        // Validate the Grip-Sig header:
        if (!GripControl.validateSig(session.getHeaders().get("grip-sig"), "<key>"))
            return newFixedLengthResponse(Response.Status.UNAUTHORIZED, null,
                    "invalid grip-sig token");

        // Instruct the client to long poll via the response headers:
        List<Channel> channels = Arrays.asList(new Channel("<channel>"));
        Response response = newFixedLengthResponse(Response.Status.OK, null, null);
        response.addHeader("grip-hold", "response");
        response.addHeader("grip-channel", GripControl.createGripChannelHeader(channels));
        // To optionally set a timeout value in seconds:
        // session.addHeader("grip-timeout", "<timeout_value>");

        return response;
    }
}
```

Long polling example via response _body_ using the NanoHTTPD web server. The client connects to a GRIP proxy over HTTP and the proxy forwards the request to the origin. The origin subscribes the client to a channel and instructs it to long poll via the response _body_.

```java
package com.example;

import java.util.*;
import java.io.IOException;
import fi.iki.elonen.NanoHTTPD;
import org.fanout.gripcontrol.*;
import org.fanout.pubcontrol.*;

public class App extends NanoHTTPD {

    public App() throws IOException {
        super(80);
        start();
    }

    public static void main(String[] args) {
        try {
            new App();
        }
        catch (IOException exception) { }
    }

    @Override
    public Response serve(IHTTPSession session) {
        // Validate the Grip-Sig header with a base64 encoded key:
        if (!GripControl.validateSig(session.getHeaders().get("grip-sig"), "<key>"))
            return newFixedLengthResponse(Response.Status.UNAUTHORIZED, null,
                    "invalid grip-sig token");

        // Instruct the client to long poll via the response body:
        List<Channel> channels = Arrays.asList(new Channel("<channel>"));
        String holdResponse = GripControl.createHoldResponse(channels);
        // To optionally set a timeout value in seconds:
        // holdResponse = GripControl.createHoldResponse(channels, null, <timeout_value>);
        Response response = newFixedLengthResponse(Response.Status.OK, null, holdResponse);
        response.addHeader("content-type", "application/grip-instruct");

        return response;
    }
}
```

WebSocket example using the NanoHTTPD web server. A client connects to a GRIP proxy via WebSockets and the proxy forward the request to the origin. The origin accepts the connection over a WebSocket and responds with a control message indicating that the client should be subscribed to a channel. Note that in order for the GRIP proxy to properly interpret the control messages, the origin must provide a 'grip' extension in the 'Sec-WebSocket-Extensions' header. This is accomplished in the NanoHTTPD server by overriding the serve(IHTTPSession) method.

```java
package com.example;

import java.util.*;
import java.io.IOException;
import fi.iki.elonen.*;
import fi.iki.elonen.NanoWSD.WebSocketFrame.*;
import org.fanout.gripcontrol.*;
import org.fanout.pubcontrol.*;
import java.lang.Thread;
import java.lang.InterruptedException;

public class App extends NanoWSD {

    public App() throws IOException {
        super(80);
        start();
    }

    public static void main(String[] args) {
        try {
            new App();
        }
        catch (IOException exception) { }
    }

    // Override the serve method to ensure that the 'Sec-WebSocket-Extensions' header is sent:
    @Override
    public Response serve(final IHTTPSession session) {
        Response response = super.serve(session);
        response.addHeader("Sec-WebSocket-Extensions", "grip; message-prefix=\"\"");
        return response;
    }

	@Override
	protected WebSocket openWebSocket(IHTTPSession handshake) {
		return new GripWebSocket(this, handshake);
	}

	private static class GripWebSocket extends WebSocket {
		private final App server;

		public GripWebSocket(App server, IHTTPSession handshakeRequest) {
			super(handshakeRequest);
			this.server = server;
		}

		@Override
		protected void onOpen() {
            // Create channel hash map:
            Map<String, Object> channel = new HashMap<String, Object>();
            channel.put("channel", "<channel>");

            // Subscribe the WebSocket to a channel:
            try {
                send("c:" + GripControl.webSocketControlMessage("subscribe", channel));
            } catch (IOException exception) { }

            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException exception) { }

            // Publish a message to the subscribed channel:
            Map<String, Object> entry = new HashMap<String, Object>();
            entry.put("control_uri", "<myendpoint_url>");
            List<Map<String, Object>> config = Arrays.asList(entry);
            GripPubControl pub = new GripPubControl(config);
            List<String> channels = Arrays.asList("<channel>");
            List<Format> formats = Arrays.asList((Format)new WebSocketMessageFormat("WebSocket test publish!"));
            try {
                pub.publish(channels, new Item(formats, null, null));
            } catch (PublishFailedException exception) {
                System.err.println("Publish failed: " + exception);
            }
        }

		@Override
		protected void onClose(CloseCode code, String reason, boolean initiatedByRemote) { }

		@Override
		protected void onMessage(WebSocketFrame message) { }

		@Override
		protected void onPong(WebSocketFrame pong) { }

		@Override
		protected void onException(IOException exception) { }
	}
}
```

WebSocket over HTTP example using the NanoHTTPD web server. In this case, a client connects to a GRIP proxy via WebSockets and the GRIP proxy communicates with the origin via HTTP.

```java
package com.example;

import java.util.*;
import java.io.IOException;
import fi.iki.elonen.*;
import org.fanout.gripcontrol.*;
import org.fanout.pubcontrol.*;
import java.lang.Thread;

public class App extends NanoHTTPD {

    public App() throws IOException {
        super(80);
        start();
    }

    public static void main(String[] args) {
        try {
            new App();
        }
        catch (IOException exception) { }
    }

    // A helper class for publishing a message on a separate thread:
    private class PublishMessage implements Runnable {
        public void run() {
            Thread.sleep(1000);
            Map<String, Object> entry = new HashMap<String, Object>();
            entry.put("control_uri", "<myendpoint_uri>");
            List<Map<String, Object>> config = Arrays.asList(entry);
            GripPubControl pub = new GripPubControl(config);
            List<String> channels = Arrays.asList("<channel>");
            List<Format> formats = Arrays.asList(
                    (Format)new WebSocketMessageFormat("WebSocket test publish!"));
            try {
                pub.publish(channels, new Item(formats, null, null));
            } catch (PublishFailedException exception) {
                System.err.println("Publish failed: " + exception);
            }
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        // Validate the Grip-Sig header with a base64 encoded key:
        if (!GripControl.validateSig(session.getHeaders().get("grip-sig"), "<key>"))
            return newFixedLengthResponse(Response.Status.UNAUTHORIZED, null, "invalid grip-sig token");

        // Only allow the POST method:
        Method method = session.getMethod();
        if (!Method.POST.equals(method))
            return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, null, null);

        // Parse the request body:
        Map<String, String> body = new HashMap<String, String>();
        try {
            session.parseBody(body);
        } catch (IOException exception) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, null, null);
        } catch (ResponseException exception) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, null, null);
        }

        // Decode the WebSocket events.
        // Note: appending a new line is required because NanoHTTPD automatically
        // trims whitespace from the post data.
        List<WebSocketEvent> inEvents = GripControl.decodeWebSocketEvents(body.get("postData") + "\r\n");

        String responseBody = "";
        if (inEvents != null && inEvents.size() > 0 && inEvents.get(0).type.equals("OPEN")) {
            // Create channel hash map:
            Map<String, Object> channel = new HashMap<String, Object>();
            channel.put("channel", "<channel>");

            // Open the WebSocket and subscribe it to a channel:
            List<WebSocketEvent> outEvents = new ArrayList<WebSocketEvent>();
            outEvents.add(new WebSocketEvent("OPEN"));
            outEvents.add(new WebSocketEvent("TEXT", "c:" +
                    GripControl.webSocketControlMessage("subscribe", channel)));
            responseBody = GripControl.encodeWebSocketEvents(outEvents);

            // Publish a message to the subscribed channel:
            new Thread(new PublishMessage()).start();
        }

        Response response = newFixedLengthResponse(Response.Status.OK, null, responseBody);

        // Set the headers required by the GRIP proxy:
        response.addHeader("content-type", "application/websocket-events");
        response.addHeader("sec-websocket-extensions", "grip; message-prefix=\"\"");

        return response;
    }
}
```

Parse a GRIP URI to extract the URI, ISS, and key values. The values will be returned in a map containing 'control_uri', 'control_iss', and 'key' keys.

```java
 Map<String, Object> config = GripControl.parseGripUri(
        "https://api.fastly.com/service/<my-service>?key=<fastly-api-token>")
```
