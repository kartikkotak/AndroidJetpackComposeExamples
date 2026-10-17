# Compose examples

Home links to the component playground, Lists and Grids, and Carousels.

- Buttons demonstrates the standard Material 3 button and icon button variants, toggle icon buttons, floating action buttons, segmented selection, disabled states, and a simulated loading action.
- Text Fields demonstrates filled/outlined fields, keyboard actions, password visibility, validation, multiline input, read-only, and disabled fields. Form validation is local; no form data is sent.
- Interactive List demonstrates add, remove, selection, bulk deletion, and empty state. Its ViewModel persists items and selection through SavedStateHandle.
- Photo List and Photo Grid demonstrate stable item keys and photo selection.
- Horizontal Pager and the snapping LazyRow carousel share paginated photos and lazy image loading.

## Photo architecture

`PicsumPhotoApi → DefaultPhotoRepository → PhotoViewModel → PhotoExamplesScreen`

The app-scoped repository is constructed in ExamplesApplication and passed into destination-scoped ViewModels through a factory. Screens observe StateFlow with lifecycle-aware collection. The photo API runs off the main thread, has connection/read timeouts, and maps response DTOs into a UI-independent Photo model. The repository caches up to ten pages in memory. Coil provides image memory/disk caching separately.

The public service is https://picsum.photos/v2/list?page=1&limit=20; no API key is required. Cards request bounded 800×600 images by ID and display author attribution. Photo metadata is loaded again after process death; cached metadata is not persisted to disk. Local editing does not mutate remote photos.

Near-end scrolling requests the next page. A loading guard prevents overlapping requests. Errors retain existing content and require explicit retry. Refresh reloads page one, clears selection on success, and invalidates the repository page cache. An unsuccessful refresh retains displayed photos; Retry repeats the refresh. A short page marks the end.

## Verification

Run `./gradlew :app:testDebugUnitTest :app:assembleDebug` for local validation.
Run `./gradlew :app:connectedDebugAndroidTest` with an emulator for navigation and list interaction checks. Navigation tests inject photo metadata so they do not depend on the live photo service; image failures remain non-blocking.

## Advanced UI: Location and Map

Open **Home → Advanced UI → Location and Map**. The example uses Maps Compose and Google Play services' fused location provider. Tap **Use my location** to request a single location fix; permission is requested only after that action. Approximate permission is supported. Requests are cancelled when the screen stops, with a bounded timeout. No background permission or continuous location tracking is used.

The screen displays coordinates and accuracy, centers a marker on a successful fix, switches between normal/satellite maps, and lets you long-press to drop a separate pin. Location settings and App permissions links handle disabled location and permission denial. Without a fix, the map starts at San Francisco; this is a sample viewport, not the device's position.

### Map setup

1. Create a Google Cloud project with billing configured, enable **Maps SDK for Android**, and create an API key: https://developers.google.com/maps/documentation/android-sdk/get-api-key.
2. Restrict the key to Maps SDK for Android and your Android package/signing certificate. The debug package is `com.example.androidjetpackcomposeexamples`; `./gradlew :app:signingReport` shows certificate fingerprints.
3. Add `MAPS_API_KEY=your_key_here` to the root **local.properties** (already ignored by Git). Alternatively supply a user Gradle property or `MAPS_API_KEY` environment variable. Never commit a key.
4. Rebuild the app. An empty key shows an explanatory setup card instead of initializing the map. A configured but invalid/restricted key requires fixing its Google Cloud configuration; a nonempty value alone cannot verify authorization.

Location retrieval does not require the Maps key, but both examples require working Google Play services. For emulator testing use a Google APIs/Play Store image, enable device location, and set coordinates in the emulator's Extended controls → Location. Map tiles and satellite imagery need network access and a valid authorized key.

The SDK's standard location layer is deliberately disabled: only the explicit foreground location action fetches a location, then a normal map marker displays the result. The selected long-press pin survives configuration changes. Device location is retained in its destination ViewModel while the process lives, and cleared when permission is revoked; it is not written to disk.

Architecture: `DeviceLocationProvider → LocationMapViewModel → LocationMapScreen`. Runtime permissions, lifecycle observation, camera state, and map gestures stay in the UI. Provider and ViewModel are independently testable through the `LocationProvider` interface.

## Appearance settings

Settings controls System/Light/Dark appearance, Default/Ocean/Forest/Purple palettes, wallpaper-based dynamic colors, Default/Serif typography, and Rounded/Slightly rounded/Square shapes. Dynamic colors take priority over the saved palette; disable them to select a palette. Reset restores system appearance, dynamic colors, default typography/palette, and rounded shapes.

`DataStoreSettingsRepository → SettingsViewModel → SettingsApp / SettingsScreen` shares one app-scoped preferences DataStore. Updates are atomic, preserve unrelated options, and persist across restarts. SettingsApp observes preferences above the theme and waits for the initial read before creating navigation, so subsequent changes recompose the existing navigation tree. Unknown stored option names fall back to defaults. Write failures are shown on Settings; read I/O failures fall back to defaults.

The preview, menu cards, standard button examples, text fields, and cards use the selected Material theme. Component-specific shapes such as icon buttons and segmented controls retain their intended control shapes. Google map tiles, satellite imagery, and photo content retain their original colors.
