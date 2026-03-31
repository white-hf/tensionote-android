# Tensionote Play Store Launch Assets

This directory contains Android release-prep materials for Google Play:

- `fastlane/metadata/android/`
  Store listing copy for supported locales.
- `assets-source/`
  Source artwork for the launcher icon, feature graphic, and phone screenshots.
- `data-safety-draft.md`
  First-pass Google Play Data safety answers.
- `support-contact-template.md`
  Support contact placeholders to replace before publication.
- `final-upload-steps.md`
  Minimal release upload sequence for the release owner.
- `privacy-policy.md`
  Publishable privacy policy draft for a website or GitHub Pages page.
- `release-checklist.md`
  Final Android launch checklist.

Notes:

- Google Play requires PNG or JPG for screenshots and feature graphics.
- Source files here are SVG so they can be edited and exported later without quality loss.
- Export screenshots at `1080x1920`.
- Export the feature graphic at `1024x500`.
- Export the app icon at `512x512`.
- The generated release bundle should be uploaded from `android/app/build/outputs/bundle/release/`.
- The generated release APK, if needed for internal review, is in `android/app/build/outputs/apk/release/`.
