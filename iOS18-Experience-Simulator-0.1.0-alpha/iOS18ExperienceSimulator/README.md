# iOS 18 Experience Simulator for Android

**Status:** 0.1.0-alpha — initial functional foundation  
**Target:** Android 11 / API 30 and newer  
**Minimum Android:** API 26  
**Build:** GitHub Actions (`.github/workflows/build.yml`)

An independent Android application that presents an interactive environment inspired by iOS 18. It is a simulator of interface patterns and selected interactions; it does not run Apple's iOS binaries or replace Android.

## Current implementation

- Simulated Lock Screen with clock, date, sample notification area and swipe-up transition.
- Home Screen with two pages, app grid, dock, wallpaper variants and Control Center gesture.
- Control Center with persistent simulated Wi-Fi, Bluetooth, Airplane Mode and Focus switches, plus simulated brightness and volume sliders.
- Settings screens for appearance, wallpaper, performance profile and the experimental glass lab.
- Photos grid with demonstration tiles and image import through the Android system photo picker; imported URI permissions are retained where the provider supports persistence.
- Camera screen that delegates capture to the Android camera app through `TakePicturePreview` and displays the returned preview.
- Phone with fictional contacts and simulated calls.
- Messages with an in-app demonstration conversation.
- Safari-style WebView with URL bar, back, forward and reload.
- Clock, Calendar, Notes, Calculator, Weather demo and App Store demo.
- Local persistence for main simulator settings, Notes and selected photo references.
- High Quality, Balanced and Low Resource profiles that adjust panel/icon elevation and screen-transition duration.
- GitHub Actions workflow that compiles a debug APK and uploads it as an artifact.

Some applications are deliberately marked as demonstrations. Simulated toggles do not change Android's real radio, volume or brightness state.

## Build with GitHub Actions

1. Create an empty repository on GitHub.
2. Extract this project ZIP and open a terminal inside the `iOS18ExperienceSimulator` folder.
3. Initialize Git and push the complete folder (including the hidden `.github/workflows/build.yml` file):

```bash
git init
git add .
git commit -m "Initial iOS 18 simulator project"
git branch -M main
git remote add origin https://github.com/SEU-USUARIO/SEU-REPOSITORIO.git
git push -u origin main
```

4. Open the repository's **Actions** tab. The `Build Android APK` workflow runs automatically after the push. You can also start it manually with **Run workflow**.
5. Open the completed workflow run and download the artifact named `ios18-simulator-debug-apk`.
6. Extract `app-debug.apk`, transfer it to the phone and install it. Android may ask you to allow installation from the app used to open the APK.

The workflow runs the calculator unit tests and builds on GitHub-hosted Ubuntu using JDK 17, Android SDK 36 and Gradle 8.13. It does not require a Gradle wrapper in this initial project because Gradle is installed by `gradle/actions/setup-gradle`.

## Build locally

Install JDK 17, Android SDK Platform 36, Build Tools 36.0.0 and Gradle 8.13. Then run:

```bash
gradle --no-daemon testDebugUnitTest assembleDebug
```

The APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Technology choices

- Kotlin 2.3.21
- Android Gradle Plugin 8.13.2
- Gradle 8.13
- Jetpack Compose BOM 2026.09.00
- AndroidX Activity Compose 1.13.0
- `minSdk 26`, `targetSdk 35`, `compileSdk 36`

The app targets Android 11 compatibility but compiles against a newer SDK. Runtime compatibility is controlled primarily by `minSdk` and by avoiding APIs unavailable on the target device.

## HakiOS reference

The public HakiOS README and NOTICE were reviewed. HakiOS is an experimental XNU/iOS 26 bring-up for the Xiaomi 15; it includes kernel patches, EFI, userland and GPU translation work. Those components are not an Android UI toolkit and cannot simply be placed in an APK. This initial app does not import HakiOS kernel, EFI, userland or GPU source code. HakiOS is currently used as a technical reference only.

The actual HakiOS source archive was not attached to this conversation, and the build environment used for this initial artifact had no outbound network access. Therefore, no claim is made that the full source tree was audited or ported.

## Visual version policy

The default visual target is iOS 18. Liquid Glass is associated with iOS 26 and is kept separate as an optional experimental inspiration. On Android 11/API 30, the initial glass surfaces use translucency, gradients and borders; they do not claim to provide true backdrop blur. Android's Compose blur support for surface content requires Android 12/API 31 or later.

## Known limitations in 0.1.0-alpha

- Not an iOS emulator and does not execute Apple system binaries.
- System UI and many bundled app visuals are original approximations, not exact reproductions.
- Photos supports importing selected pictures through the Android system photo picker. Full automatic indexing of the device library and album metadata are not implemented yet. Some document providers may not support persistent URI grants, in which case an imported item may need to be selected again after its permission expires.
- Camera delegates capture to the Android camera application instead of embedding a live CameraX preview.
- Phone calls, messages, radio toggles, brightness and volume are simulated.
- Weather and App Store contents are demonstration data.
- Safari uses Android WebView, not Apple's Safari/WebKit build for iOS.
- Full HakiOS source was not available in the working files for direct code-level audit.
- The application has not yet been built or physically tested on a Galaxy A10. GitHub Actions is provided for the first real build.

## Research references

- HakiOS project: https://github.com/superboyyy/hakios-arm64e-dada
- HakiOS README: https://github.com/superboyyy/hakios-arm64e-dada/blob/main/README.md
- HakiOS licensing and notices: https://github.com/superboyyy/hakios-arm64e-dada/blob/main/NOTICE.md
- Android Compose state: https://developer.android.com/develop/ui/compose/state
- Compose animations: https://developer.android.com/develop/ui/compose/animation/quick-guide
- Compose blur compatibility: https://developer.android.com/develop/ui/compose/graphics/blur/overview
- CameraX: https://developer.android.com/codelabs/camerax-getting-started
- Bitmap memory optimization: https://developer.android.com/topic/performance/graphics/load-bitmap.html
- Android Photo Picker: https://developer.android.com/training/data-storage/shared/photo-picker
- Android Gradle Plugin compatibility: https://developer.android.com/build/releases/agp-8-13-0-release-notes
- Compose BOM: https://developer.android.com/develop/ui/compose/bom
- GitHub Actions + Gradle: https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-gradle

## Disclaimer

This is an independent Android project inspired by publicly observable interface patterns. It is not affiliated with or endorsed by Apple Inc. iOS, iPhone, Safari and other Apple marks belong to their respective owners.
