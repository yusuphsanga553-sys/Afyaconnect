# Afyaconnect Production Architecture

## Clients
- Android patient app
- Android doctor app (initially can share one APK with role-based UI)
- Web admin console

## API layer
- API gateway / reverse proxy
- OAuth2/OIDC authentication
- RBAC: PATIENT, DOCTOR, ADMIN, SUPPORT, FINANCE
- Rate limiting and abuse controls

## Services
- User & identity service
- Practitioner verification service
- Doctor availability/scheduling service
- Matching service
- Consultation service
- Messaging service
- Realtime communications token service
- Clinical documentation service
- Prescription/referral service
- Payment & ledger service
- Notification service
- Audit/compliance service

## Data stores
- Relational database for users, consultations, ledger and audit metadata
- Encrypted object storage for patient-uploaded files
- Realtime messaging store or broker
- Cache for availability and matching

## Matching order
1. Required specialty / scope of practice
2. Verification status
3. Current availability
4. Qualification / expertise
5. Preferred language
6. Consultation price / patient budget
7. Rating / service quality signals
8. Location when in-person referral or follow-up is relevant

Never allow location alone to outrank clinical suitability.

## Security baseline
- TLS everywhere
- Encrypt sensitive records at rest
- Short-lived access tokens + refresh-token rotation
- MFA for doctors/admins
- No phone-number exposure between patient and doctor
- Signed URLs for files
- Append-only clinical/audit log strategy
- Least-privilege access
- Consent and purpose limitation
- Redaction of sensitive information from application logs
