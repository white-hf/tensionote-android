# Tensionote Android Final Upload Steps

Use this document for the last release preparation pass before submitting to Google Play.

## 1. Build the release bundle

From the repo root:

```powershell
.\gradlew.bat :android:app:bundleRelease
```

Expected output:

- Android App Bundle: `android/app/build/outputs/bundle/release/app-release.aab`
- Release APK, if generated for internal review: `android/app/build/outputs/apk/release/`

## 2. Verify the release bundle locally

- Confirm the app launches on a test device.
- Confirm the launcher icon renders correctly.
- Confirm the version name and version code are what you expect.
- Confirm no debug-only UI remains.

## 3. Prepare Play Console listing

- App name: `Tensionote`
- Default language: English
- Localized listing copy:
  - `play-store/fastlane/metadata/android/en-US/`
  - `play-store/fastlane/metadata/android/zh-CN/`
  - `play-store/fastlane/metadata/android/hi-IN/`
- Visual assets:
  - `play-store/assets-source/icon/`
  - `play-store/assets-source/feature-graphic/`
  - `play-store/assets-source/screenshots/`

## 4. Fill store questionnaires

- Data safety: use `play-store/data-safety-draft.md`
- Support contact: use `play-store/support-contact-template.md`
- Privacy policy: publish `play-store/privacy-policy.md` to a public URL

## 5. Upload and review

- Upload the signed `app-release.aab`.
- Upload screenshots and feature graphic exports.
- Verify localized text in each language section.
- Review the content rating and app access answers.
- Submit for review.

## 6. After submission

- Keep the support contact active.
- Keep the privacy policy URL public.
- Track any policy feedback from Google Play and update the docs first, then the listing.
