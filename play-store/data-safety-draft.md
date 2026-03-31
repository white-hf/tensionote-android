# Tensionote Android Data Safety Draft

Use this as the first-pass draft for Google Play Console Data safety.

## Summary

- Data collected automatically by the app: No
- Data shared automatically by the app: No
- Account creation required: No
- Server-side sync in V1: No
- Advertising SDKs: No
- Analytics SDKs: No

## User-Initiated Sharing

The app lets the user export and share a blood pressure report by email or by another app chosen by the user.

- The app does not upload the report to a server.
- The app passes the exported file only when the user taps share.
- Third-party apps may process the shared file according to their own policies.

## Data Stored Locally on Device

The app stores the following data on-device:

- Blood pressure measurements
- Heart rate values
- Measurement timestamps
- Tags and notes
- Reminder schedules
- App language preference
- Exported PDF reports

## Security Notes

- No data transmission to the app developer's server in V1
- No account sign-in or backend identity system
- Local notification reminders are stored on device only

## Suggested Play Console Answers

- Does the app collect or share personal data? No
- Is data transmitted off device by default? No
- Can the user initiate export/share? Yes
- Is the app usable without an account? Yes
- Is encrypted transport applicable? Not applicable for V1

## Final Human Review Before Submission

- Confirm the Play Console questionnaires match the final app behavior.
- Confirm no third-party SDKs have been added after this draft was written.
- Confirm privacy policy wording matches the final final release build.
