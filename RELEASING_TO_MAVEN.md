To release this to Maven:

This release was tested with Maven 3.6.3 and JDK 14.0.1.

You will need to create a `settings.xml` file at M2_HOME that provides the following information.

```xml
<settings>
    <servers>
        <server>
            <id>sonatype-nexus-snapshots</id>
            <username>[--username--]</username>
            <password>[--password--]</password>
        </server>
        <server>
            <id>sonatype-nexus-staging</id>
            <username>[--username--]</username>
            <password>[--password--]</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>release-sign-artifacts</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <gpg.keyName>[--keyname--]</gpg.keyName>
                <gpg.passphrase>[--passphrase--]</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
</settings>
```

After doing so, you should be able to release to Maven using the commands

```shell script
mvn clean
mvn release:prepare
mvn release:perform
```

Followed by

```shell script
git push-tags
git push origin master
```

Then check sonatype at https://oss.sonatype.org/#stagingRepositories

Close and Release the package to complete.
