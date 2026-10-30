# Tamil Nadu Smart Hospital (தமிழ்நாடு ஸ்மார்ட் மருத்துவமனை)

A complete, responsive Java web application for discovering hospitals and doctors and booking demo appointments across **Tamil Nadu, India**. Built with **Spring Boot 3**, **Spring Data JPA**, **Thymeleaf**, **Spring Security**, and an embedded **H2 database**.

---

## ⚠️ Important Demo Notice & Safety Principles
> **Demo website. Hospital listings and appointment availability are sample data and must be verified directly with the hospital.**
- **No Real Medical Records:** In compliance with healthcare privacy regulations, medical history, clinical diagnoses, and prescriptions are neither collected nor stored.
- **No Sensitive Identifiers:** No government identification numbers (Aadhaar, Voter ID, PAN) or payment details (UPI/Credit Cards) are requested.
- **Fictional Seeded Data:** All hospital names (e.g., *"Demo City General Hospital"*, *"Sample Care Centre"*), doctor profiles, and time slots are synthetic demonstration records.
- **Emergency Medical Contact in Tamil Nadu:** For real medical emergencies, call **108** (Statewide Ambulance) or **104** (Tamil Nadu Health Helpline).

---

## 🌟 Key Features

### 1. Patient Portal & Discovery
- **Statewide Search:** Search and filter sample medical facilities across all 38 districts of Tamil Nadu (Chennai, Coimbatore, Madurai, Tiruchirappalli, Salem, Tirunelveli, Vellore, Thanjavur, etc.).
- **Specialty Directory:** Quick access to common medical departments: Cardiology, General Medicine, Orthopedics, Pediatrics, Neurology, Dermatology, Gastroenterology, and Ophthalmology.
- **Doctor Profiles & Real-time Slots:** Inspect credentials, languages spoken (Tamil, English), consultation mode, and selectable morning and evening demo slots.
- **Demo Booking & Slip:** Concurrency-checked appointment booking with a unique reference code (`TNSH-YYYYMMDD-XXXX`), inline validation, printable demo slip, and cancellation support.
- **Patient Dashboard:** View active and past demo visits, real-time simulation alerts, and a transparent records section clearly indicating no medical charts are kept.

### 2. Doctor Portal
- **Consultation Queue:** View booked demo appointments, patient names, and contact numbers.
- **Status Updates:** Mark demo appointments as **Completed** or **Cancelled** (which automatically reopens the time slot for other patients).
- **Slot Management:** Create custom time slots for any date, delete unbooked slots, or trigger an instant 7-day slot generation.

### 3. State Health Administration Console
- **System Metrics:** Live overview of total hospitals, specialist doctors, total bookings, and registered users.
- **Audit Table:** Filter appointments across Tamil Nadu by specific date, hospital, doctor, or status (CONFIRMED, COMPLETED, CANCELLED).
- **Listing Management:** Add, edit, or activate/deactivate fictional hospital and doctor profiles.
- **Batch Slot Generator:** Quickly seed 7 to 30 days of standard 30-minute consultation slots for any doctor.

---

## 🔑 Pre-Seeded Demo Accounts & 1-Click Login

On the `/login` page, you can either click the **1-Click Quick Demo Login** buttons for instant access or use the following pre-seeded credentials:

| Role | Name | Email | Password | Access Level |
|---|---|---|---|---|
| **Patient** | Selvan Kumar | `patient@demo.tn.gov.in` | `demo123` | Search, Book, View Dashboard, Cancel Appointments |
| **Doctor** | Dr. K. Anitha, MD, DM | `doctor@demo.local` | `demo123` | Queue, Mark Completed, Manage Time Slots |
| **Admin** | TN Health Admin | `admin@demo.local` | `admin123` | Central Administration, Hospital/Doctor CRUD, H2 DB |

*New visitors can also register as a new patient via the `/register` page.*

---

## 🚀 Running the Application Locally

### Prerequisites
- **Java Development Kit (JDK):** Version 17, 21, or 26.
- **Maven:** Apache Maven 3.9+ (An embedded Maven 3.9.6 wrapper `mvnw.cmd` / `mvnw` is included in the project root).

### 1. Build and Run
From the project directory, run:

#### Windows (PowerShell or Command Prompt):
```powershell
.\mvnw.cmd spring-boot:run
```

#### Linux / macOS:
```bash
./mvnw spring-boot:run
```

Alternatively, build an executable JAR and run it directly:
```bash
.\mvnw.cmd clean package -DskipTests
java -jar target/tamilnadu-smart-hospital-1.0.0-SNAPSHOT.jar
```

### 2. Access the Portal
- **Application URL:** [http://localhost:8080](http://localhost:8080)
- **H2 In-Memory Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - **JDBC URL:** `jdbc:h2:mem:smarthospitaldb`
  - **User Name:** `sa`
  - **Password:** *(leave blank)*

---

## 🏗️ Project Architecture & Data Model

Organized into clean, idiomatic Spring Boot packages:
```
com.tn.smarthospital
├── config             # SecurityConfig (BCrypt, role-based filters, CSRF) & DataInitializer
├── controller         # Spring MVC controllers for Home, Hospital, Doctor, Booking, Dashboards
├── dto                # Form and Request objects with Jakarta validation constraints
├── model              # JPA Entities (Hospital, Doctor, AvailabilitySlot, Appointment, User, Notification)
├── repository         # Spring Data JPA repositories with custom JPQL queries
└── service            # Business services handling thread-safe booking, cancellations, notifications
```

### Double-Booking Prevention
The platform enforces race condition prevention at both the **Service layer** (synchronization & atomic status check) and the **Database layer** (unique constraints on `AvailabilitySlot` and `@OneToOne` foreign key mapping on `Appointment`).

---

## 📱 TextBee.dev SMS Gateway Integration

Tamil Nadu Smart Hospital integrates with the **[TextBee.dev](https://textbee.dev)** SMS gateway to dispatch real-time informational SMS notifications to patients when demo appointments are booked, cancelled, or updated.

### 1. TextBee Account & Device Setup
1. **Sign Up:** Register for an account at [https://textbee.dev](https://textbee.dev).
2. **Download & Connect Android Gateway App:** Install the official TextBee Android APK on an Android device that will act as your SMS sender.
3. **Register Device:** Log in to the Android app; your device will register with your TextBee account and appear in your dashboard.
4. **Locate Device ID:** In your [TextBee Dashboard](https://textbee.dev/dashboard), navigate to **Devices** and copy your **Device ID**.
5. **Generate API Key:** In the dashboard, navigate to **API Settings** > **API Keys**, generate a new secret API key, and copy it.

### 2. Environment Variables Configuration
To ensure zero credential leaks, all credentials must be stored strictly in environment variables and never committed to source control:

| Variable | Required | Default | Description |
|---|---|---|---|
| `TEXTBEE_API_KEY` | Yes (when enabled) | *(empty)* | Your private TextBee API key (sent via `x-api-key` header) |
| `TEXTBEE_DEVICE_ID` | Yes (when enabled) | *(empty)* | The target Android Device ID registered in your TextBee dashboard |
| `TEXTBEE_API_URL` | No | `https://api.textbee.dev/api/v1` | Official TextBee API base URL |
| `TEXTBEE_ENABLED` | No | `false` | Set to `true` to activate live SMS dispatch |

#### Local Development Setup (.env file)
A template file `.env.example` is provided in the repository root:
```env
TEXTBEE_API_KEY=your_textbee_api_key_here
TEXTBEE_DEVICE_ID=your_textbee_device_id_here
TEXTBEE_API_URL=https://api.textbee.dev/api/v1
TEXTBEE_ENABLED=true
```
Create a local `.env` file (which is already included in `.gitignore`):
```powershell
# In PowerShell (Windows):
$env:TEXTBEE_API_KEY="your_actual_api_key"
$env:TEXTBEE_DEVICE_ID="your_device_id"
$env:TEXTBEE_ENABLED="true"
.\mvnw.cmd spring-boot:run
```
```bash
# In Linux / macOS:
export TEXTBEE_API_KEY="your_actual_api_key"
export TEXTBEE_DEVICE_ID="your_device_id"
export TEXTBEE_ENABLED="true"
./mvnw spring-boot:run
```

### 3. SMS Notification Triggers & Formats
All SMS messages are dispatched **after** database transactions successfully commit and explicitly indicate that they pertain to sample demo appointments:

1. **Appointment Booked:**
   ```
   Hello {patientName}, your demo appointment has been booked successfully.

   Doctor: {doctorName} Hospital: {hospitalName} Date: {appointmentDate} Time: {appointmentTime}

   Booking Reference: {bookingReference}

   Important: This is a demo booking and is NOT confirmed with the hospital. Please verify directly with the hospital.

   Tamil Nadu Smart Hospital
   ```
2. **Appointment Cancelled:**
   ```
   Hello {patientName}, your demo appointment has been cancelled.

   Booking Reference: {bookingReference}

   Tamil Nadu Smart Hospital
   ```
3. **Appointment Status Updated:**
   Sent when a doctor marks an appointment completed or cancelled from their dashboard.

### 4. Admin SMS Diagnostic & Testing Tool
Hospital administrators can manage and test the SMS gateway from the Admin Console (`/admin/dashboard`):
- **Live Status:** Displays whether TextBee is `ENABLED` or `DISABLED`.
- **Credential Masking:** The API Key is masked on the server (e.g. `tb_live_...3829`) and is never sent to the browser in raw format.
- **Diagnostic SMS Dispatch:** Send a real test SMS to any designated 10-digit mobile number with immediate delivery feedback.
- **Security Guidance:** Prominent warnings reminding administrators to maintain credentials server-side only.

### 5. Automated Tests & Mocking
The test suite in `com.tn.smarthospital.service.TextBeeSmsServiceTest` covers all 7 edge scenarios without sending real SMS or hitting external endpoints:
1. **SMS API Success:** Verifies payload structure (`{"recipients":["+91..."],"message":"...","deviceId":"..."}`) and `x-api-key` header with HTTP 200 mock.
2. **Invalid API Key:** Verifies proper handling and user-friendly error capturing when the gateway returns HTTP 401 Unauthorized.
3. **Network Failure:** Simulates timeout and connection dropouts, verifying graceful fallback.
4. **SMS Disabled:** Verifies zero network calls occur when `TEXTBEE_ENABLED=false`.
5. **Booking Despite SMS Failure:** Guarantees that appointment creation and time slot reservation succeed even if the SMS gateway is unreachable or fails.
6. **Cancellation Notification:** Verifies SMS dispatch when an appointment is cancelled by a patient or doctor.
7. **Duplicate Notification Prevention:** Prevents double-send when forms are repeatedly submitted or on duplicate requests.

To run the automated SMS test suite:
```powershell
.\mvnw.cmd test -Dtest=TextBeeSmsServiceTest
```

### 6. Security & Privacy Safeguards
- **Zero Source Credential Leaks:** No API keys are hardcoded in Java code, properties, HTML, JavaScript, or Thymeleaf templates.
- **Log Sanitation:** Patient mobile numbers are masked in application logs (e.g., `******3210`).
- **Resilient Booking Flow:** SMS dispatch runs inside safe try-catch blocks and will never rollback a patient's appointment booking.
- **Disclaimer Banner:** Every booking confirmation presents the mandatory patient advisory:
  > *"Demo booking — not confirmed with the hospital. SMS notifications are informational only."*

---

## 🛠️ Transitioning to a Verified Production System

To transition this demonstration prototype into a state-grade production portal:

1. **Production Database:**
   - Replace the embedded H2 database with a managed PostgreSQL or MySQL cluster.
   - Configure connection pooling via HikariCP and database migrations via Flyway or Liquibase.

2. **Integration with Official Tamil Nadu Health Registries:**
   - Replace fictional demo data with verified hospital registries using the **National Health Facility Registry (HFR)** and **Tamil Nadu Health System Reform Program (TNHSRP)** APIs.
   - Authenticate medical practitioners through the **National Healthcare Professionals Registry (HPR)**.

3. **Digital Identity & ABHA (Ayushman Bharat Digital Mission):**
   - Integrate ABHA (Health ID) creation and OAuth2 OTP verification for patient accounts.
   - Secure patient communications using transactional SMS gateways (e.g. CDAC SMS Seva / NIC Gateway).

4. **Production Security Hardening:**
   - Move secrets out of code and configuration into environment variables or secret managers (e.g., AWS Secrets Manager, HashiCorp Vault).
   - Enforce HTTPS/TLS with strict HSTS, Content Security Policy (CSP), and WAF rate-limiting.
   - Implement audit logging for all patient scheduling transactions.

