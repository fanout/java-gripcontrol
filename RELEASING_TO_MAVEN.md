## Releasing to Maven Central Repository

This package is released to Maven Central Repository by using Maven with the `central-publishing-maven-plugin`.

This process has been tested with OpenJDK 17.0.17 and Maven 3.9.12.

This is released under the `org.fanout` group ID, which is maintained by Fastly.

### Credentials

This package is published to Maven Central using a User Token under a Maven Central Repository account.

### GPG key

Publishing requires you to prove your identity using GPG. Create a GPG keypair for the email associated with your Maven Central Repository account.

Once you have published your GPG public key, note its key name and passphrase.

## Configure Maven

You will need copy `./.mvn/settings.example.xml` to `./.mvn/settings.xml`, and fill in the following using the User Token credentials and your GPG keyname and passphrase.  Do not commit this file to source control.

```xml
<settings>
    <servers>
        <server>
            <id>central</id>
            <!-- Note: these are for the User Token, not the Maven Central Repository login -->
            <username>**USERNAME**</username>
            <password>**PASSWORD**</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>central</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <gpg.keyName>**KEYNAME**</gpg.keyName>
                <gpg.passphrase>**PASSPHRASE**</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
</settings>
```

## Release

The following steps should be taken on each release.

1. Update docs + version
    - Update `README.md` / `CHANGELOG.md`
    - Bump `<version>` in `pom.xml`
    - Commit (replace 1.0.0 below with the version you're releasing)
      ```shell
      git commit -m "Release 1.0.0"
      ```
2. Tag the release
    - Create a tag
      ```shell
      git tag v1.0.0
      ```
3. Push commit + tag
     ```shell
     git push
     git push --tags
     ```
4. Validate that tests pass, everything builds, sources/javadoc are valid, and GPG signing works.
   ```shell
   mvn clean verify
   ```
5. Release to Maven using the commands:
   ```shell
   mvn clean deploy
   ```

   If everything succeeds, you'll see messages that end with lines such as:
   ```
   [INFO] Uploaded bundle successfully, deployment name: Deployment, deploymentId: befad37c-d2f7-4583-92e8-33f23b9c88a3. Deployment will require manual publishing
   [INFO] Waiting until Deployment befad37c-d2f7-4583-92e8-33f23b9c88a3 is validated
   [INFO] Deployment befad37c-d2f7-4583-92e8-33f23b9c88a3 has been validated. To finish publishing visit https://central.sonatype.com/publishing/deployments
   [INFO] ------------------------------------------------------------------------
   [INFO] BUILD SUCCESS
   [INFO] ------------------------------------------------------------------------
   [INFO] Total time:  9.341 s
   [INFO] Finished at: 2025-12-19T02:04:28+09:00
   [INFO] ------------------------------------------------------------------------
   ```

6. Complete publishing.
    - Visit https://central.sonatype.com/publishing/deployments. You should see:
        - Deployment ID, matching the deployment ID in the output above
        - Status: Validated
    - Click the **Publish** button.
