# Google Play Console Submission Draft

Use this as the first-pass submission worksheet for Tensionote on Google Play.

## App Basics

- App name:
  - English: `Tensionote`
  - Simplified Chinese: `Tensionote 血压记录`
  - Hindi: `Tensionote Blood Pressure`
- Default language:
  - English
- Category:
  - Health & Fitness
- Application ID:
  - `com.tensionote`

## Store Listing Files

- English title:
  - `play-store/fastlane/metadata/android/en-US/title.txt`
- English short description:
  - `play-store/fastlane/metadata/android/en-US/short_description.txt`
- English full description:
  - `play-store/fastlane/metadata/android/en-US/full_description.txt`
- Simplified Chinese listing:
  - `play-store/fastlane/metadata/android/zh-CN/`
- Hindi listing:
  - `play-store/fastlane/metadata/android/hi-IN/`

## Visual Assets

- App icon source:
  - `play-store/assets-source/icon/tensionote-app-icon.svg`
- Feature graphic source:
  - `play-store/assets-source/feature-graphic/tensionote-feature-graphic.svg`
- Screenshot sources:
  - `play-store/assets-source/screenshots/`
- Export guide:
  - `play-store/assets-source/export-guide.md`

## Build Artifacts

- Release APK output:
  - `android/app/build/outputs/apk/release/`
- Release AAB output:
  - `android/app/build/outputs/bundle/release/`

## Preliminary Console Answers

- App access:
  - No login required.
- Ads:
  - No.
- Target audience:
  - Adults using a personal health-management tool.
- News app:
  - No.
- Health features:
  - Personal recording and management only.
- Medical claim boundary:
  - Do not claim diagnosis, treatment, or replacement of a doctor.

## Data Safety Draft

This draft still needs final human review before submission.

- Does the app collect data and send it off device?
  - Planned answer: No.
- Is data shared with third parties by the app automatically?
  - Planned answer: No.
- Does the app allow the user to export or share files manually?
  - Yes, user-initiated report sharing only.
- Is account creation required?
  - No.
- Is data encrypted in transit?
  - Not applicable for current V1 because there is no server transmission by the app.

## Public URLs Still Needed

- Privacy policy public URL
- Support email or support website

## Final Human Checks Before Submission

- Review all health wording in every language.
- Replace placeholder support contact in the privacy policy.
- Export final PNG/JPG visual assets from the SVG source files.
- Verify the signed release AAB on the target release machine.
