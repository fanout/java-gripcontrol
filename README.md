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
```

HTTPS Publishing
----------------

Note that on some operating systems Java may require you to add the root CA certificate of the publishing server to the key store. This is particularly the case with OSX. Follow the steps outlined in this article to address the issue: http://nodsw.com/blog/leeland/2006/12/06-no-more-unable-find-valid-certification-path-requested-target

Also, if using Java 6 you may run into SNI issues. If this occurs we recommend HTTP-only publishing or upgrading to Java 7 or above.

Usage
-----

```java
```
