## Releasing to Maven Central

This package is released to Maven Central via
[Sonatype OSSRH](https://central.sonatype.org/publish/publish-guide/) by using Maven with the 
`nexus-staging-maven-plugin`.

This has been tested with OpenJDK 17.0.2 and Maven 3.9.6.

This is released under the `org.fanout` group ID, which is maintained by Fastly.

### Credentials

> NOTE: Sonatype is moving away from Jira soon.

At the time of this writing, this package is published to Maven Central using
a Sonatype "legacy" Jira account created at https://issues.sonatype.org.
See [Registering Via Legacy](https://central.sonatype.org/register/legacy/) in
The Central Repository docs for details.

When publishing, OSSRH will share the same email, username, and password as this account.
If you need to update your email, username, or password, [do it in Jira](https://central.sonatype.org/register/legacy/#review-requirements).

### GPG key

Publishing requires you to prove your identity using GPG. Create a GPG keypair
for the email associated with your Jira account. See [GPG](https://central.sonatype.org/publish/requirements/gpg/#review-requirements)
in The Central Repository docs for details.

Once you have published your GPG public key, note its key name and
passphrase.

### Configure Maven

> TODO: This currently uses username and password, but in the future
> Sonatype is moving to requiring tokens for publishing.

You will need to add your credentials to `~/.m2/settings.xml`:
```xml
<settings>
    <servers>
        <server>
            <id>ossrh</id>
            <username>*USERNAME*</username>
            <password>*PASSWORD*</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>ossrh</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <gpg.keyName>*KEYNAME*</gpg.keyName>
                <gpg.passphrase>*PASSPHRASE*</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
</settings>
```

Increment the pom.xml <version> tag to the version to release.

After doing so, you should be able to release to Maven using the commands
```shell script
mvn clean deploy
```

If everything succeeds, you'll see a message such as:

```
[INFO] Remote staging repositories are being released...

Waiting for operation to complete...
......

[INFO] Remote staging repositories released.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  02:41 min
[INFO] Finished at: 2024-01-24T01:47:13+09:00
[INFO] ------------------------------------------------------------------------
```

### Wrap up

Commit the pom.xml (with the updated version number),
and then add a tag and push to GitHub.
