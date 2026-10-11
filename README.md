# Afyaconnect Android MVP

Afyaconnect is a native Android telemedicine marketplace prototype for Tanzania. It connects patients to verified doctors based on specialty, current availability, qualification, language, consultation fee and, when clinically relevant, location.

## What is implemented

- Patient, Doctor and Administrator entry flows
- Symptom description with simple emergency red-flag screening
- Specialty selection and basic symptom-to-specialty suggestion
- Doctor ranking by availability, verification, price and rating
- Verified-doctor profile UI
- Prepaid consultation flow with Tanzania-oriented payment-method placeholders
- Secure-consultation UI prototype with chat plus voice/video/file integration placeholders
- Patient consultation history prototype
- Doctor availability and earnings dashboard
- Doctor clinical workspace for assessment, advice, medication and referral
- Administrator credential-verification queue
- Clear production warnings around clinical safety, payments and credential verification

## Technology

- Native Android
- Kotlin / Jetpack Compose
- Material 3
- Compile SDK 37 / Target SDK 37 / Min SDK 26
- Android Gradle Plugin 9.4.0
- Gradle 9.6.0
- Compose BOM 2026.09.00

## Open in Android Studio

1. Extract the project.
2. Open the `Afyaconnect` folder in a recent Android Studio version supporting AGP 9.4.
3. Allow Gradle sync to download dependencies.
4. Run the `app` configuration on an emulator or Android device.

The ZIP does not include the Gradle wrapper JAR. Android Studio can sync the project using its Gradle tooling; you can also generate a wrapper locally with Gradle 9.6 if desired.

## Important: prototype vs production

The current app uses in-memory demo data. It does **not** yet provide real authentication, real doctor-registry lookup, production payments, encrypted server storage, real voice/video calling, e-prescribing, push notifications, or production audit logging.

Before live clinical use, implement the following:

1. Backend API + database with patient/doctor/admin RBAC.
2. Identity, MFA and secure session management.
3. Doctor verification against authoritative professional registration/licensing records.
4. Server-side payment initiation, callback verification, settlement, refunds and receipts.
5. WebRTC or a healthcare-capable communications provider for in-app voice/video.
6. Encryption in transit and at rest, secure file storage, audit trails and device-security controls.
7. Consent, privacy notices, data-retention rules and Tanzanian personal-data compliance review.
8. Clinically governed emergency/red-flag protocols; do not rely on keyword matching alone.
9. Prescription/referral governance and anti-fraud controls.
10. Monitoring, incident response, backups and disaster recovery.

## Suggested backend modules

- Auth & identity
- Practitioner verification
- Patient profiles
- Doctor profiles, schedules & availability
- Matching/search
- Consultation lifecycle
- Chat & attachments
- Realtime voice/video tokens
- Clinical documentation
- Prescriptions/referrals
- Payments/ledger/payouts
- Ratings & complaints
- Notifications
- Admin & audit reporting

## Recommended production consultation state machine

`REQUESTED -> PAYMENT_PENDING -> PAID -> DOCTOR_ACCEPTED -> IN_PROGRESS -> COMPLETED`

Alternative terminal states: `CANCELLED`, `EXPIRED`, `REFUNDED`, `REFERRED_EMERGENCY`.

## Disclaimer

This repository is an MVP/prototype and must not be used as a clinical system without security, regulatory, privacy, payment and clinical-governance review.
