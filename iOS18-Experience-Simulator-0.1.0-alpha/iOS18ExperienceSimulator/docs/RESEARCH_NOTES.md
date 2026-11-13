# Research Notes — 0.1.0-alpha

## HakiOS findings

The HakiOS repository describes an experimental bring-up that boots Apple's open-source XNU kernel `xnu-12377.121.6` on Xiaomi 15 hardware and runs an iOS 26 arm64e user space. Its documented layout separates `kernel/`, `base/`, `userland/`, `uefi-dada/`, `gpu2/` and `tools/`. The README documents device-specific boot and GPU work; it does not describe an Android application framework.

The NOTICE file identifies different licensing regimes: APSL 2.0 for kernel/base/userland work, BSD-2-Clause-Patent for UEFI changes, MIT for tools/docs, and GPL/LGPL/MIT terms for individual GPU patches. Therefore, this project does not wholesale-copy the HakiOS tree into the APK. No HakiOS source file is imported in 0.1.0-alpha.

## Android decisions

- Kotlin + Jetpack Compose for the native interactive UI.
- A single simulator route/state owner keeps Lock Screen, Home Screen, Control Center and app navigation coherent.
- SharedPreferences are used for the small set of initial local settings. This is intentionally simpler for the first alpha; a repository abstraction can migrate to DataStore later.
- WebView is configured without file/content access and without JavaScript window opening. JavaScript remains enabled for normal modern websites; no JavaScript bridge is exposed.
- Camera capture is delegated to the system camera contract. The simulator does not request camera permission in this alpha.

## Android 11 / graphics

Android 11 is API 30. Android `RenderEffect` and Compose's standard blur rendering require API 31 or newer. Therefore the default glass-like panels in this release use semi-transparent fills, gradients and subtle borders. They are approximations, not true backdrop blur or optical refraction.

## Performance decisions

Photos uses generated demo tiles and imports user-selected images through Android Photo Picker. Imported image thumbnails are decoded off the main thread and downsampled to a bounded size to reduce bitmap allocations on low-memory devices. Selected content URIs are saved locally; persistent access is requested when supported by the provider. The Home Screen loads only two small app pages.

## Sources consulted

- HakiOS README: https://github.com/superboyyy/hakios-arm64e-dada/blob/main/README.md
- HakiOS NOTICE: https://github.com/superboyyy/hakios-arm64e-dada/blob/main/NOTICE.md
- Compose state: https://developer.android.com/develop/ui/compose/state
- Compose state hoisting: https://developer.android.com/develop/ui/compose/state-hoisting
- Compose animation guide: https://developer.android.com/develop/ui/compose/animation/quick-guide
- Compose blur: https://developer.android.com/develop/ui/compose/graphics/blur/overview
- Android Photo Picker and device availability: https://developer.android.com/training/data-storage/shared/photo-picker
- CameraX: https://developer.android.com/codelabs/camerax-getting-started
- Bitmap efficiency: https://developer.android.com/topic/performance/graphics/load-bitmap.html
- Android Gradle Plugin 8.13: https://developer.android.com/build/releases/agp-8-13-0-release-notes
- Compose BOM: https://developer.android.com/develop/ui/compose/bom
- GitHub Actions Gradle guide: https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-gradle

## Verification boundary

The full HakiOS ZIP was not present in the working files and outbound network access from the build container was unavailable. Findings about HakiOS are based on its public README and NOTICE pages, not a line-by-line inspection of all source files. The project has not been compiled in this environment because Android SDK/Gradle are not installed. The included GitHub Actions workflow is intended to perform that first CI build.
