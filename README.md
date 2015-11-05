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
  <version>1.0.0</version>
</dependency>
<dependency>
  <groupId>org.fanout</groupId>
  <artifactId>pubcontrol</artifactId>
  <version>1.0.7</version>
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
        entry.put("control_uri", "https://api.fanout.io/realm/<realm>");
        entry.put("control_iss", "<realm>");
        entry.put("key", DatatypeConverter.parseBase64Binary("<key>"));
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

Validate the Grip-Sig request header from incoming GRIP messages. This ensures that the message was sent from a valid source and is not expired. Note that when using Fanout.io the key is the realm key, and when using Pushpin the key is configurable in Pushpin's settings. The key is passed in base64 encoded format.

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
