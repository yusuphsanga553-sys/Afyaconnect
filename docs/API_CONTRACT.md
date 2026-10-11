# Suggested REST API Contract

## Authentication
POST /v1/auth/register/patient
POST /v1/auth/register/doctor
POST /v1/auth/login
POST /v1/auth/refresh
POST /v1/auth/logout

## Doctors
GET  /v1/doctors?specialty=&available=&language=&maxFee=&region=
GET  /v1/doctors/{doctorId}
PUT  /v1/doctors/me/availability
PUT  /v1/doctors/me/schedule
POST /v1/doctors/me/credentials

## Verification
GET  /v1/admin/verifications?status=pending
POST /v1/admin/verifications/{doctorId}/approve
POST /v1/admin/verifications/{doctorId}/reject

## Consultations
POST /v1/consultations
GET  /v1/consultations/{id}
POST /v1/consultations/{id}/accept
POST /v1/consultations/{id}/start
POST /v1/consultations/{id}/complete
POST /v1/consultations/{id}/refer

## Messaging
GET  /v1/consultations/{id}/messages
POST /v1/consultations/{id}/messages
POST /v1/consultations/{id}/attachments

## Realtime calling
POST /v1/consultations/{id}/voice/token
POST /v1/consultations/{id}/video/token

## Clinical records
POST /v1/consultations/{id}/clinical-note
POST /v1/consultations/{id}/prescription
POST /v1/consultations/{id}/referral

## Payments
POST /v1/payments/consultations/{id}/initiate
POST /v1/payments/provider/callback
GET  /v1/payments/{paymentId}
POST /v1/admin/payments/{paymentId}/refund

## Reviews
POST /v1/consultations/{id}/review

## Safety
POST /v1/triage/check

All sensitive mutations should carry authenticated actor identity, correlation ID, timestamp and audit metadata server-side.
