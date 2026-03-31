# Tensionote Android

Android native app for Tensionote.

## Tech Stack

- Kotlin
- Jetpack Compose
- Local storage
- Local reminders
- PDF export
- Email/share flow

## Build

Open this repository root in Android Studio, or run:

```powershell
.\gradlew.bat :android:app:assembleDebug
```

Release build:

```powershell
.\gradlew.bat :android:app:assembleRelease
```

Release bundle for Google Play:

```powershell
.\gradlew.bat :android:app:bundleRelease
```

## Notes

- Supports Simplified Chinese, English, and Hindi
- V1 is local-only storage
- Home screen is focused on quick entry and last 2 weeks trend
- Play Store launch assets and localized listing copy live under `play-store/`
- Release AAB is generated under `android/app/build/outputs/bundle/release/`
