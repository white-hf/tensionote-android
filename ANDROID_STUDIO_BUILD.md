# Tensionote Android Studio Build Guide

## Open The Project

Open the repository root in Android Studio:

`D:\code\BloodPressionMaster`

Do not open only `android\app`.
The Gradle root project is the repository root.

## Project Files Already Present

The Android Studio project is already configured with:

- `settings.gradle.kts`
- `build.gradle.kts`
- `gradle.properties`
- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`
- `android/app/build.gradle.kts`
- `local.properties`

## Build In Android Studio

1. Open `D:\code\BloodPressionMaster`
2. Wait for Gradle sync to finish
3. Build with `Build > Make Project`
4. Run with `Run > Run 'app'`

## Build From Command Line

From the repository root:

```powershell
.\gradlew.bat :android:app:assembleDebug
```

## Current Debug APK

Latest debug APK:

`D:\code\BloodPressionMaster\android\app\build\outputs\apk\debug\app-debug.apk`

## Android SDK Path

This project currently points to:

`C:\Users\tang pc\AppData\Local\Android\Sdk`

That path is stored in:

`D:\code\BloodPressionMaster\local.properties`

If your SDK location changes, update `local.properties`.

## Current Status

- Android debug build is passing
- Gradle wrapper is present and working
- Android Studio can open the repository root directly
- iOS source exists, but iOS build cannot be validated on this Windows machine
