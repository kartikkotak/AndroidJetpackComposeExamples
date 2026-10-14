# Android Jetpack Compose Examples

A collection of small Android UI examples built with Kotlin, Jetpack Compose, and Material 3. The project is intended for learning and experimenting with Compose layouts, components, and UI patterns.

## Current examples

The project currently contains a starter app with an empty Material 3 scaffold, a greeting composable, and a preview. More examples can be added as the collection grows.

| Example | Source | Preview |
| --- | --- | --- |
| Greeting | [MainActivity.kt](app/src/main/java/com/example/androidjetpackcomposeexamples/MainActivity.kt) | `GreetingPreview` |

The greeting is available in the Android Studio preview; it is not currently displayed in the running app.

## Getting started

1. Clone this repository and open its root directory in Android Studio.
2. Install Android SDK Platform 37 through the SDK Manager. The app currently compiles and targets API 37.
3. Allow Gradle to sync and download dependencies. The checked-in Gradle daemon configuration requests JDK 25.
4. Select an emulator or device running Android 13 (API 33) or newer, then run the `app` configuration.

To explore the greeting, open `MainActivity.kt` and use the Compose preview for `GreetingPreview`.

Build a debug APK from the repository root:

```sh
./gradlew assembleDebug
```

On Windows, use `gradlew.bat assembleDebug`.

## Project structure

```text
app/src/main/java/com/example/androidjetpackcomposeexamples/
├── MainActivity.kt       # App entry point and initial example
└── ui/theme/             # Compose colors, typography, and theme
gradle/libs.versions.toml # Dependency and plugin versions
```

## Adding examples

- Give each example a descriptive composable name and its own source file or package.
- Include a `@Preview` so the UI can be explored in Android Studio.
- Add an entry to the examples table with a source link and preview name.
- Add screenshots or GIFs under `docs/images/` when they help demonstrate the UI.
- Keep examples focused and document any required permissions, dependencies, or setup.

## Repository contents

Commit source code, resources, Gradle build configuration, and the complete Gradle wrapper, including its JAR. Local SDK paths, build output, caches, and signing credentials are excluded by `.gitignore`.

## License

This project is licensed under the [MIT License](LICENSE).
