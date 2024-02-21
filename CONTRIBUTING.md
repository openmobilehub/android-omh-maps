# How to contribute

Bug reports and pull requests from users are what keep this project working.

## Basics

1. Create an issue and describe your idea
2. [Fork it](https://github.com/openmobilehub/omh-maps/fork)
3. Create your feature branch (`git checkout -b my-new-feature`)
4. Commit your changes (`git commit -am 'Add some feature'`)
5. Publish the branch (`git push origin my-new-feature`)
6. Create a new Pull Request

## Development

For using the plugins for development locally, there are two approaches.

### Using local modules in the project

This scenario utilizes the local modules (subprojects) residing inside `packages/` to be used in place of dependencies so that between modifying the code in a plugin and running it in the sample app there is no need to publish to Maven.

This project has been pre-configured with such conditional configuration that can be enabled as follows:

- Via root project's `local.properties` (applies both to Android Studio and `gradlew`): add `useLocalProjects=true`
- Via a CLI flag: `./gradlew -PuseLocalProjects=true ...`

### Using Maven Local
This scenario includes primarily three things to be achieved compared to standard development scenario:

- `repositories` need to include `mavenLocal()`
- publishing needs to happen to maven local
- signing needs to be disabled for publishing

This project has been pre-configured with such conditional configuration that can be enabled as follows:

- Via root project's `local.properties` (applies both to Android Studio and `gradlew`): add `useMavenLocal=true`
- Via a CLI flag: `./gradlew -PuseMavenLocal=true ...`

Once you have made a change in any of the `packages/core`, `packages/core-google maps` or `packages/plugin-openstreetmap` modules, you must `publishToMavenLocal` in that module in order to see the changes.

#### Publishing to Maven Local

##### Step 1: Publish the plugins to Maven Local

###### With Android Studio 

- Open the `Gradle` tab and run the `publishToMavenLocal` for modules: `packages > core`, `packages > plugin-googlemaps` and `packages > plugin-openstreetmap`:

![gradle-core](https://github.com/openmobilehub/omh-maps/assets/124717244/7a8aeb52-fcf2-4c8c-a0e8-e249e69b3fea)
![gradle-core-gms](https://github.com/openmobilehub/omh-maps/assets/124717244/e5a370d9-1429-4234-a884-b39a23c6dadb)
![gradle-core-ngms](https://github.com/openmobilehub/omh-maps/assets/124717244/2cc52110-8faa-47e3-9298-a6cec846a348)

**Note**: to publish all modules in `packages/`, you can simply run the task `publishToMavenLocal` in the root project. Please also remember to publish the `core` module first.

###### With the CLI:

- first publish `./gradlew :packages:core:publishToMavenLocal`
- then:
  - to publish all other modules: `./gradlew publishToMavenLocal`
  - to publish a selected module: `./gradlew :packages:{module}:publishToMavenLocal`

**Note**: to publish all modules in `packages/`, you can simply run the task `publishToMavenLocal` in the root project directory. Please also remember to publish the `core` module first.

##### Step 2: Verify plugin is published

Go to `/Users/your_user/.m2` dot folder and you'll find the plugin.

##### Step 3: Debug

Add some prints to debug the code

##### Step 4: Test it

Create a sample project, add the plugin and sync the project with gradle and you'll see logs in the `Build` tab in Android Studio.

## Checking your work

You can verify your code with the following tasks:

```
./gradlew assemble
./gradlew detekt
```

## Write documentation

This project has documentation in a few places:

### Introduction and usage

A friendly `README.md` files written for many audiences:
- [root project](README.md)
- [core](packages/core/README.md)
- [plugin-googlemaps](packages/plugin-googlemaps/README.md)
- [plugin-openstreetmap](packages/plugin-openstreetmap/README.md)

### Examples and advanced usage

You can find more information in the advanced sections of each package:
- [core](packages/core/docs/advanced/)
- [plugin-googlemaps](packages/plugin-googlemaps/docs/advanced/)
- [plugin-openstreetmap](packages/plugin-openstreetmap/docs/advanced/)

## Releasing a new version

1. Clone the repository
2. Update the changelog (and commit it afterwards)
3. Push the changes and wait for the latest CI build to complete
4. Bump the version, create a Git tag and commit the changes
5. Push the version bump commit: `git push`
6. Push the Git tag: `git push --tags`
