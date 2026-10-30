# CuraTransit – Smart Hospital Finder & Medical Transit System
### (தமிழ்நாடு ஸ்மார்ட் மருத்துவமனை மற்றும் மருத்துவப் பயண வழிகாட்டி)

A full-stack, responsive Java web application for finding hospitals and doctors, comparing critical care and emergency capacity, accessing outstation patient travel assistance, and booking demo appointments across **Tamil Nadu, India**.

Built with **Java 17**, **Spring Boot 3.3**, **Spring MVC**, **Thymeleaf**, **Spring Data JPA**, **Spring Security**, and an embedded **H2 database** with zero external frontend runtime dependencies (HTML, CSS, vanilla browser JavaScript only). Fully pre-configured for free cloud deployment on **Render**.

---

## ⚠️ Important Demo Notice & Safety Principles
> **Demo website. Hospital listings, transit directions, ICU capacity, and appointment availability are sample data and must be verified directly with the hospital.**
- **No Real Medical Records:** In compliance with healthcare privacy regulations, medical history, clinical diagnoses, and prescriptions are neither collected nor stored.
- **No Sensitive Identifiers:** No government identification numbers (Aadhaar, Voter ID, PAN) or payment details (UPI/Credit Cards) are requested.
- **Fictional Seeded Data:** All hospital names (e.g., *"Demo City General Hospital"*, *"Sample Care Centre"*), doctor profiles, and time slots are synthetic demonstration records.
- **Emergency Medical Contact in Tamil Nadu:** For real medical emergencies, call **108** (Statewide Ambulance) or **104** (Tamil Nadu Health Helpline).

---

## 🌟 Key Features of CuraTransit

### 1. Hospital Discovery, Filtering & Sorting
- **Statewide Coverage:** Search hospitals across all 38 districts of Tamil Nadu (Chennai, Coimbatore, Madurai, Tiruchirappalli, Salem, Tirunelveli, Vellore, Thanjavur, etc.).
- **Keyword & Department Search:** Search by facility name, address keywords, or medical specialty.
- **🚨 24x7 Emergency Filter:** Instant one-click filter for hospitals equipped with 24x7 trauma care and casualty services.
- **🛏️ ICU Bed Availability Filter:** Filter hospitals with active Intensive Care Units and see available bed capacity metrics.
- **Multi-Criteria Sorting:** Sort facilities dynamically by Hospital Name (A-Z), District, ICU Bed Capacity (High to Low), or Doctor Count.

### 2. ⚖️ Side-by-Side Hospital Comparison Matrix
- **Compare 2 to 4 Hospitals:** Select hospitals from the list or via dedicated comparison selectors.
- **Interactive Comparison Table (`/hospitals/compare`):**
  - Emergency 24x7 availability and trauma triage
  - ICU bed capacity and critical monitoring units
  - Wheelchair accessibility, entrance ramps, and elevators
  - Proximity to nearest Railway Stations, Airports, and Bus Stands
  - Patient attendant lodging & guest house guidance
  - Multilingual staff support & language coordination
  - Transit help desk phone lines
  - Associated doctor counts and active OPD departments

### 3. 🚆 Medical Travel Assistance for Outstation Patients
- **Transit Hub Mapping:** Every hospital details page features verified distances and transit instructions from nearest railway junctions (e.g., Chennai Central, Coimbatore Junction), airports (e.g., MAA, CJB, IXM, TRZ), and central bus terminals (CMBT, Mattuthavani, Gandhipuram).
- **Lodging Guidance:** Budget guest house, lodge, and family dormitory recommendations for attendants accompanying patients from other districts or states.
- **Dedicated Transit Help Desk:** Direct simulated telephone coordination contact numbers.

### 4. ♿ Accessibility & Multilingual Support
- **Wheelchair Assistance:** Request wheelchair support at hospital arrival during appointment booking.
- **Language Coordinator Request:** Outstation patients can request on-site translators / interpreters for **Tamil, English, Telugu, Hindi, Malayalam, or Kannada**.

### 5. Specialist Doctor Directory & OPD Booking
- **Doctor Profiles:** Qualifications, clinical experience, spoken languages, and demonstration OPD chamber numbers.
- **Real-Time Slot Engine:** Morning and afternoon time slots with concurrency double-booking prevention.
- **Demo Booking Reference:** Generates instant verifiable reference codes (`TNSH-YYYYMMDD-XXXX`).
- **Printable Slip:** Clean printable patient consultation slip with QR/reference tokens and full hospital transit instructions.
- **Patient Dashboard (`/patient/dashboard`):** View upcoming demo visits, review requested transit assistance, cancel reservations, or view previous appointment slips.

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

## 🌐 Complete REST API Endpoints

CuraTransit provides clean JSON REST APIs for mobile applications and third-party healthcare aggregators:

### Hospital APIs
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/hospitals` | Search & filter hospitals (`?district=...&hasEmergency=true&hasIcu=true&sortBy=...`) |
| `GET` | `/api/hospitals/{id}` | Get single hospital details |
| `GET` | `/api/hospitals/compare?ids=1,2` | Side-by-side comparison for 2 to 4 hospitals |
| `GET` | `/api/hospitals/{id}/transit` | Outstation travel assistance & transit hub directions |

### Doctor APIs
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/doctors` | List doctors (`?hospitalId=...&specialty=...&district=...`) |
| `GET` | `/api/doctors/{id}` | Get doctor profile and credentials |
| `GET` | `/api/doctors/{id}/slots` | Get available OPD slots for date (`?date=YYYY-MM-DD`) |

### Appointment APIs
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/appointments` | Book demo appointment with outstation travel assistance options |
| `GET` | `/api/appointments/my` | View logged-in patient's appointments |
| `GET` | `/api/appointments/{reference}` | Get appointment details by booking reference |
| `POST` | `/api/appointments/{id}/cancel` | Cancel demo appointment and reopen time slot |

#### Sample Booking REST API Request:
```json
POST /api/appointments
Content-Type: application/json

{
  "slotId": 12,
  "patientName": "Ravi Shankar",
  "patientPhone": "9840123456",
  "patientEmail": "ravi@sample.local",
  "reason": "Cardiology OPD consultation",
  "outstationPatient": true,
  "travelOrigin": "Tiruchirappalli, Tamil Nadu",
  "needsWheelchair": true,
  "needsLanguageSupport": true,
  "preferredLanguage": "Tamil",
  "confirmDemoNotice": true
}
```

---

## ☁️ Deploying on Render (Free Web Service)

CuraTransit is fully configured for deployment on **[Render](https://render.com)**.

### Configuration Files Included:
- **`system.properties`**: Declares `java.runtime.version=17` so Render automatically selects Java 17.
- **`render.yaml`**: Pre-configured Render Blueprint with free web service definitions.
- **`application.properties`**: Dynamic port binding via `server.port=${PORT:8080}`.
- **Executable JAR**: Maven builds `target/curatransit-1.0.0.jar`.

### Option A: 1-Click Blueprint Deployment via Git
1. Push this repository to your GitHub or GitLab account.
2. Log in to [Render Dashboard](https://dashboard.render.com).
3. Click **New +** > **Blueprint**.
4. Select your repository. Render will automatically detect `render.yaml` and configure the service.
5. Click **Apply**.

### Option B: Manual Web Service Setup on Render
1. Go to [Render Dashboard](https://dashboard.render.com) > **New +** > **Web Service**.
2. Connect your Git repository.
3. Configure the following settings:
   - **Name:** `curatransit` (or your chosen name)
   - **Region:** Any (e.g. *Oregon, US* or *Frankfurt, EU*)
   - **Branch:** `main`
   - **Runtime:** `Java`
   - **Build Command:**
     ```bash
     mvn clean package -DskipTests
     ```
   - **Start Command:**
     ```bash
     java -jar target/curatransit-1.0.0.jar
     ```
   - **Instance Type:** `Free`
4. In **Environment Variables**, add:
   - `PORT`: `10000` (Render will override this automatically)
   - `JAVA_VERSION`: `17`
   - `TEXTBEE_ENABLED`: `false` (or `true` if you have TextBee credentials)
   - `TEXTBEE_API_KEY`: *(Optional TextBee API Key)*
   - `TEXTBEE_DEVICE_ID`: *(Optional TextBee Device ID)*
5. Click **Create Web Service**. Your application will be live in 2-3 minutes at `https://<your-service-name>.onrender.com`.

---

## 🚀 Running Locally

### Prerequisites
- **Java:** JDK 17 or higher.
- **Maven:** Apache Maven 3.9+ (A wrapper `mvnw.cmd` / `mvnw` is included in the project root).

### 1. Build and Run
```powershell
# Windows:
.\mvnw.cmd spring-boot:run

# Linux / macOS:
./mvnw spring-boot:run
```

Alternatively, build and run the packaged JAR:
```powershell
.\mvnw.cmd package -DskipTests
java -jar target/curatransit-1.0.0.jar
```

### 2. Access the Application
- **Web Portal:** [http://localhost:8080](http://localhost:8080)
- **H2 In-Memory Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - **JDBC URL:** `jdbc:h2:mem:curatransitdb`
  - **Username:** `sa`
  - **Password:** *(empty)*

---

## 📱 TextBee.dev SMS Integration

CuraTransit integrates with the **[TextBee.dev](https://textbee.dev)** SMS gateway to dispatch real-time SMS notifications to patients when demo appointments are booked, cancelled, or updated.

### Configuration (`.env` or Environment Variables):
```env
TEXTBEE_API_KEY=your_textbee_api_key_here
TEXTBEE_DEVICE_ID=your_textbee_device_id_here
TEXTBEE_API_URL=https://api.textbee.dev/api/v1
TEXTBEE_ENABLED=true
```

- When `TEXTBEE_ENABLED=false` (default), the application operates safely in simulation mode with zero external network requests.
- When enabled, notifications are sent directly through the patient's registered mobile number without interfering with booking transactions.
- Zero credential exposure: Keys are read strictly from environment variables and never logged or exposed in the frontend.

---

## 🧪 Automated Test Suite

Run the automated JUnit 5 test suite:
```powershell
.\mvnw.cmd test
```
Tests cover:
- TextBee SMS API payload serialization and authorization header verification
- Invalid API key (HTTP 401) handling and timeout resilience
- Concurrency reservation and booking rollback protection
- Spring Context loading and database integrity checks

---

## 🏛️ Project Structure

```
curatransit
├── .env.example                               # Environment template for SMS credentials
├── system.properties                          # Java 17 declaration for Render
├── render.yaml                                # Render deployment blueprint
├── pom.xml                                    # Maven configuration & dependencies
├── src/main/java/com/tn/smarthospital
│   ├── SmartHospitalApplication.java          # Spring Boot main class
│   ├── config
│   │   ├── DataInitializer.java              # Fictional seed data (12 hospitals, 14 doctors, 980 slots)
│   │   └── SecurityConfig.java               # Spring Security, RBAC & CSRF rules
│   ├── controller
│   │   ├── HomeController.java                # Landing page & about routes
│   │   ├── HospitalController.java            # Hospital listing, detail & comparison routes
│   │   ├── DoctorController.java              # Doctor schedule & slot selection
│   │   ├── AppointmentController.java         # Booking form, confirmation slip & cancellation
│   │   ├── AuthController.java                # Sign in, demo login & registration
│   │   ├── PatientController.java             # Patient dashboard
│   │   ├── DoctorPortalController.java        # Doctor portal & OPD queue
│   │   ├── AdminController.java               # Health administration console & SMS test tools
│   │   └── api
│   │       ├── HospitalApiController.java     # REST API for hospitals, transit & comparison
│   │       ├── DoctorApiController.java       # REST API for doctors & slots
│   │       ├── AppointmentApiController.java  # REST API for bookings & cancellations
│   │       └── RestExceptionHandler.java      # Centralized API error handler
│   ├── dto                                    # Request & form DTOs with Jakarta validation
│   ├── model                                  # JPA entities (Hospital, Doctor, Slot, Appointment, User)
│   ├── repository                             # Spring Data JPA repositories with custom JPQL queries
│   └── service                                # Business logic & SMS services
└── src/main/resources
    ├── application.properties                 # Server port, database & SMS properties
    ├── static
    │   ├── css/style.css                      # Accessible healthcare teal/blue stylesheet
    │   └── js/app.js                          # Floating compare bar & interactive form toggles
    └── templates                              # Thymeleaf server-rendered templates
```
