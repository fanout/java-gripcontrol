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
        channels.add("test_channel");
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

Validate the Grip-Sig request header from incoming GRIP messages. This ensures that the message was sent from a valid source and is not expired. Note that when using Fanout.io the key is the realm key, and when using Pushpin the key is configurable in Pushpin's settings.

```java
boolean isValid = GripControl.validateSig(headers["Grip-Sig"], "<key>");
```
