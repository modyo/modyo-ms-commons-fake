# Modyo Microservices Commons V2

This is a proprietary artifact made by Modyo.

Consist in a group of reusable functionalities that the microservices made by Spring Boot need for different purposes, such as:

- Utils
- Error Handling
- Logging
- Deployment
- Security

The functionalities are decoupled in three Spring Boot dependencies:

- `core`
- `http`
- `aws-api-gw`

## ¿How to use in your microservices?

### Set Up
- AdoptOpenJDK11 installed
- Githun personal access token environment variables:
    - `GITHUB_USERNAME`: Github username
    - `GITHUB_TOKEN`: Github access token with at least `read:packages` scope and Modyo SSO enabled.
    
If you don't have these credentials, follow the next step instructions, otherwise go to *Installation* Section.

### Get Github personal access token
- Follow the instruction of this
  [link](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token).
- Save the value of the token since it will not be possible to rescue it later.
- Don't forget to include the scope `read:packages`.
- Once created, go to the list of tokens click on *Enable SSO* and then on *Authorize*.
- Declare the above environment variables.

### Installation

In your microservice, go to the `build.gradle` and add the next maven repository configuration in the `respositories` section:

```groovy
repositories  {
    // ... another repositories
    maven {
        name = 'GitHubPackages'
        url = 'https://maven.pkg.github.com/modyo/maven-packages'
        credentials {
            username = System.getenv("GITHUB_USERNAME")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}
```

Then, you can add the dependencies in the `dependencies` section:

```groovy
dependencies {
    // ... another dependencies
    implementation 'com.modyo.ms.commons:core:2.x.y-RELEASE'
    implementation 'com.modyo.ms.commons:http:2.x.y-RELEASE'
    implementation 'com.modyo.ms.commons:aws-api-gateway:2.x.y-RELEASE'
}
```
Add some or all of these dependencies according to your needs.

Finally, in your Main Application class, add the following annotation:

```java
@ComponentScan({
    "com.modyo.ms.commons",
    // current application base package
    // ... base package of anther Spring Boot dependencies
})
public class Application {
    // ....
}
```

## Contributions

If you want to modify anyone of the package in this project, follow the next steps:

- Clone the `master` branch. 
- Create new branch with this format: `<feature/fix>/<Jira task code>-short-description`.
Example: `feature/SERVICES-123-new-awesome-feature`.
- Go to the folder according to the package you want to modify and open it with your IDE.
- Write your code...
- Change the version in the `build.gradle` folder according to the level of the changes.
    - Minor for new features.
    - Patch for modifications in features created previously.
- Test the code typing in your terminal: `./gradlew build`.
- Install the package in your local maven repository typing:
`./gradlew publishSnapshotPublicationToMavenLocal`.
- Use another Spring Boot microservice project to test if the package installation occurs correctly.
To do this, add Maven local repository in the `build.gradle` microservice project file:
```groovy
repositories  {
    // ... another repositories
    mavenLocal()
}
```
Then, add the dependency to test in the `dependencies` section:

```groovy
dependencies {
    // ... another dependencies
    implementation 'com.modyo.ms.commons:<dependency-to-test>:2.x.y-SNAPSHOT'
}
```
Finally, add the annotation explained in the *Installation* section.
- When everything works fine, go back to the dependency code and create a pull request that points to `master` branch.
- If the pull request is approved, merge it and create a new release with a new version tag (example: `v2.x.y`) and `master` branch.
- In your computer, checkout to `master` branch and update it.
- Publish the new version typing: `./gradlew publishReleasePublicationToGitHubPackagesRepository`.


