# GitHub Actions APK build

The workflow is located at `.github/workflows/build.yml`. It runs on pushes and pull requests targeting `main` or `master`, and can also be started manually from the Actions tab.

## What it does

1. Checks out the repository.
2. Sets up JDK 17.
3. Installs Android SDK platform 36 and build-tools 36.0.0.
4. Installs Gradle 8.13.
5. Runs `testDebugUnitTest`.
6. Runs `assembleDebug`.
7. Uploads `app/build/outputs/apk/debug/app-debug.apk` as `ios18-simulator-debug-apk`.

## Downloading the APK

Open the completed workflow run, scroll to **Artifacts**, and download `ios18-simulator-debug-apk`. Extract the artifact ZIP to obtain `app-debug.apk`.

## If a build fails

Open the failed workflow job and inspect the first Gradle error, not only the final `BUILD FAILED` line. Common causes include a dependency version no longer available, SDK package installation failure, Kotlin/Compose compiler incompatibility, or a source-level compilation error. Correct the source/configuration and push a new commit to run the workflow again.

The workflow has not yet been executed against this project. The first GitHub run is the authoritative Android/Gradle build verification.
