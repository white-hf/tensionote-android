# Android Release Checklist

## Product

- Confirm V1 scope is unchanged: quick entry, 2-week trend, history, reminders, report export, email/file sharing, settings.
- Verify all user-facing strings stay localized in English, Simplified Chinese, and Hindi.
- Review privacy policy, terms, and disclaimer text for final publishing.

## Engineering

- Run `.\gradlew.bat :android:app:assembleDebug`
- Run `.\gradlew.bat :android:app:assembleRelease`
- Verify release signing configuration on the target machine.
- Confirm app icon renders correctly on Android 13+ and round launcher surfaces.
- Confirm exported PDF opens and shares correctly on a physical Android device.
- Confirm reminder notifications work after reboot and after package update.
- Test notification-permission denied flow on Android 13+.
- Test no-email-client and no-share-target fallback behavior.

## QA

- Test English, Simplified Chinese, and Hindi.
- Test small-screen devices and long localized strings.
- Test empty-state flows for history, trend, reminders, and reports.
- Test quick entry, detailed entry, edit, delete, and detail refresh chain.

## Play Store

- Publish the privacy policy to a public URL.
- Export and upload a 512x512 app icon.
- Export and upload a 1024x500 feature graphic.
- Export and upload at least 4 phone screenshots.
- Fill in localized title, short description, and full description.
- Prepare content rating, data safety, and app access answers.
- Upload a signed `AAB`.
