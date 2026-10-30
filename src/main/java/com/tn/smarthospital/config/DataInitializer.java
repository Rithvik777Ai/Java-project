package com.tn.smarthospital.config;

import com.tn.smarthospital.model.*;
import com.tn.smarthospital.repository.*;
import com.tn.smarthospital.service.DoctorService;
import com.tn.smarthospital.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    private final AvailabilitySlotRepository slotRepository;
    private final AppointmentRepository appointmentRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final DoctorService doctorService;

    public DataInitializer(UserRepository userRepository,
                           HospitalRepository hospitalRepository,
                           DoctorRepository doctorRepository,
                           AvailabilitySlotRepository slotRepository,
                           AppointmentRepository appointmentRepository,
                           NotificationRepository notificationRepository,
                           UserService userService,
                           DoctorService doctorService) {
        this.userRepository = userRepository;
        this.hospitalRepository = hospitalRepository;
        this.doctorRepository = doctorRepository;
        this.slotRepository = slotRepository;
        this.appointmentRepository = appointmentRepository;
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.doctorService = doctorService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Data already seeded
        }

        System.out.println(">>> Seeding fictional demo data for Tamil Nadu Smart Hospital...");

        // 1. Create Demo Users
        User patientUser = userService.createUserDirectly(
                "Selvan Kumar",
                "patient@demo.tn.gov.in",
                "demo123",
                "9876543210",
                Role.ROLE_PATIENT
        );

        User doctorUser = userService.createUserDirectly(
                "Dr. K. Anitha",
                "doctor@demo.local",
                "demo123",
                "9876543211",
                Role.ROLE_DOCTOR
        );

        User adminUser = userService.createUserDirectly(
                "TN Smart Hospital Admin",
                "admin@demo.local",
                "admin123",
                "9876543212",
                Role.ROLE_ADMIN
        );

        // 2. Create Fictional Demo Hospitals strictly in Tamil Nadu
        List<Hospital> hospitals = new ArrayList<>();

        hospitals.add(new Hospital(
                "Demo City General Hospital",
                "Multi-Specialty",
                "Chennai",
                "Chennai",
                "Sample Address: No. 42, Anna Salai, Guindy, Chennai, Tamil Nadu 600032",
                "044-2200-0001 (Sample)",
                "contact@democitygeneral.local",
                "General Medicine, Cardiology, Orthopedics, Pediatrics, Neurology, Emergency Care",
                "A fictional premier multi-specialty healthcare centre located in central Chennai, equipped with sample outpatient clinics and demo consultation rooms."
        ));

        hospitals.add(new Hospital(
                "Sample Metro Medicare Hospital",
                "Super Specialty",
                "Chennai",
                "Tambaram",
                "Sample Address: No. 108, GST Road, Tambaram, Chennai, Tamil Nadu 600045",
                "044-2200-0002 (Sample)",
                "contact@samplemetromedicare.local",
                "Cardiology, Oncology, Nephrology, Gastroenterology, Pulmonology",
                "Fictional tertiary care medical center serving the southern suburban zone of Chennai with simulated advanced clinical departments."
        ));

        hospitals.add(new Hospital(
                "Sample Care Centre",
                "Specialty Hospital",
                "Coimbatore",
                "Coimbatore",
                "Sample Address: No. 75, Avinashi Road, Peelamedu, Coimbatore, Tamil Nadu 641004",
                "0422-220-0001 (Sample)",
                "contact@samplecarecentre.local",
                "General Medicine, Orthopedics, Dermatology, Obstetrics & Gynecology, Pediatrics",
                "Sample community-focused healthcare center in Peelamedu, Coimbatore, providing fictional OPD consultations and outpatient diagnostics."
        ));

        hospitals.add(new Hospital(
                "Kongu Demo Health Institute",
                "Multi-Specialty",
                "Coimbatore",
                "RS Puram",
                "Sample Address: No. 19, DB Road, RS Puram, Coimbatore, Tamil Nadu 641002",
                "0422-220-0002 (Sample)",
                "contact@kongudemohealth.local",
                "Cardiology, Neurology, Urology, General Surgery, ENT",
                "Fictional modern healthcare establishment serving western Tamil Nadu with dedicated demo outpatient facilities."
        ));

        hospitals.add(new Hospital(
                "Demo Heritage Multi-Specialty Hospital",
                "Multi-Specialty",
                "Madurai",
                "Madurai",
                "Sample Address: No. 88, Alagar Kovil Road, KK Nagar, Madurai, Tamil Nadu 625020",
                "0452-220-0001 (Sample)",
                "contact@demoheritagemadurai.local",
                "General Medicine, Cardiology, Orthopedics, Ophthalmology, Pediatrics",
                "Fictional regional referral hospital in Madurai providing demo clinical appointments across southern Tamil Nadu districts."
        ));

        hospitals.add(new Hospital(
                "Kaveri Demo Health Institute",
                "Super Specialty",
                "Tiruchirappalli",
                "Tiruchirappalli",
                "Sample Address: No. 34, Thillai Nagar Main Road, Tiruchirappalli, Tamil Nadu 620018",
                "0431-220-0001 (Sample)",
                "contact@kaveridemohealth.local",
                "Cardiology, Gastroenterology, General Medicine, Neurology, Pediatrics",
                "Fictional healthcare facility in central Tamil Nadu offering sample doctor consultations across multiple departments."
        ));

        hospitals.add(new Hospital(
                "Sample Kongu Healthcare Centre",
                "General Hospital",
                "Salem",
                "Salem",
                "Sample Address: No. 62, Fairlands Main Road, Salem, Tamil Nadu 636016",
                "0427-220-0001 (Sample)",
                "contact@samplekongusalem.local",
                "General Medicine, Orthopedics, ENT, Dermatology, Emergency Care",
                "Sample district hospital model located in Salem for demonstrating patient booking and doctor scheduling."
        ));

        hospitals.add(new Hospital(
                "Demo Royal Care Hospital",
                "Multi-Specialty",
                "Tirunelveli",
                "Tirunelveli",
                "Sample Address: No. 15, North Bypass Road, Palayamkottai, Tirunelveli, Tamil Nadu 627002",
                "0462-220-0001 (Sample)",
                "contact@demoroyaltirunelveli.local",
                "Cardiology, General Medicine, Obstetrics & Gynecology, Pediatrics, Ophthalmology",
                "Fictional medical establishment in Tirunelveli district offering demonstration booking workflows."
        ));

        hospitals.add(new Hospital(
                "Sample Fort Healthcare",
                "Community Hospital",
                "Vellore",
                "Katpadi",
                "Sample Address: No. 27, Katpadi Main Road, Vellore, Tamil Nadu 632014",
                "0416-220-0001 (Sample)",
                "contact@samplefortvellore.local",
                "General Medicine, Orthopedics, Pediatrics, Dermatology, ENT",
                "Sample clinic and outpatient community hospital in northern Tamil Nadu."
        ));

        hospitals.add(new Hospital(
                "Demo Pearl City Medical Centre",
                "Multi-Specialty",
                "Thoothukudi",
                "Thoothukudi",
                "Sample Address: No. 91, Palayamkottai Road, Thoothukudi, Tamil Nadu 628008",
                "0461-220-0001 (Sample)",
                "contact@demopearlcity.local",
                "General Medicine, Cardiology, Pulmonology, Pediatrics",
                "Fictional coastal district medical facility in Thoothukudi demonstrating online healthcare scheduling."
        ));

        hospitals.add(new Hospital(
                "Delta Demo Community Health Clinic",
                "Community Clinic",
                "Thanjavur",
                "Thanjavur",
                "Sample Address: No. 5, Medical College Road, Thanjavur, Tamil Nadu 613004",
                "04362-220-001 (Sample)",
                "contact@deltademothanjavur.local",
                "General Medicine, Pediatrics, Obstetrics & Gynecology",
                "Sample primary and outpatient health center in the Delta region of Tamil Nadu."
        ));

        hospitals.add(new Hospital(
                "Demo Nilgiris Hillside Health Centre",
                "Primary Healthcare",
                "Nilgiris",
                "Udhagamandalam",
                "Sample Address: No. 12, Commercial Road, Ooty, The Nilgiris, Tamil Nadu 643001",
                "0423-220-0001 (Sample)",
                "contact@demonilgirishealth.local",
                "General Medicine, Pulmonology, Orthopedics, Emergency Care",
                "Fictional high-altitude community healthcare clinic serving demo residents in the Nilgiris."
        ));

        hospitalRepository.saveAll(hospitals);

        // 3. Create Seeded Doctors
        List<Doctor> doctors = new ArrayList<>();

        // Doctor 1 - Linked to Dr. K. Anitha user
        Doctor d1 = new Doctor(
                "Dr. K. Anitha, MD, DM",
                "Cardiology",
                "MBBS, MD (General Medicine), DM (Cardiology)",
                14,
                hospitals.get(0), // Demo City General Hospital (Chennai)
                "Tamil, English",
                "In-Person Consultation",
                600.0,
                "Room 204, Cardiology Wing"
        );
        d1.setUser(doctorUser);
        doctors.add(d1);

        doctors.add(new Doctor(
                "Dr. M. Senthil Kumar, MS",
                "Orthopedics",
                "MBBS, MS (Orthopedics), Fellowship in Joint Replacement",
                12,
                hospitals.get(0), // Demo City General Hospital (Chennai)
                "Tamil, English, Hindi",
                "In-Person Consultation",
                550.0,
                "Room 112, Ortho OPD"
        ));

        doctors.add(new Doctor(
                "Dr. R. Priya, MD",
                "Pediatrics",
                "MBBS, MD (Pediatrics), DCH",
                9,
                hospitals.get(0), // Demo City General Hospital (Chennai)
                "Tamil, English",
                "In-Person Consultation",
                500.0,
                "Room 305, Child Care Centre"
        ));

        doctors.add(new Doctor(
                "Dr. V. Rajesh, MD",
                "General Medicine",
                "MBBS, MD (Internal Medicine)",
                15,
                hospitals.get(1), // Sample Metro Medicare (Chennai)
                "Tamil, English, Telugu",
                "In-Person Consultation",
                500.0,
                "OPD Block B, Room 10"
        ));

        doctors.add(new Doctor(
                "Dr. S. Meenakshi, MD, DM",
                "Neurology",
                "MBBS, MD (General Medicine), DM (Neurology)",
                11,
                hospitals.get(1), // Sample Metro Medicare (Chennai)
                "Tamil, English",
                "In-Person Consultation",
                700.0,
                "Neuro Clinic Room 4"
        ));

        doctors.add(new Doctor(
                "Dr. N. Karthikeyan, MS",
                "Orthopedics",
                "MBBS, MS (Orthopedics), M.Ch (Ortho)",
                16,
                hospitals.get(2), // Sample Care Centre (Coimbatore)
                "Tamil, English, Malayalam",
                "In-Person Consultation",
                600.0,
                "Room 102, Main OPD"
        ));

        doctors.add(new Doctor(
                "Dr. T. Kavitha, MD",
                "Dermatology",
                "MBBS, MD (DVL - Dermatology & Venereology)",
                8,
                hospitals.get(2), // Sample Care Centre (Coimbatore)
                "Tamil, English",
                "In-Person Consultation",
                450.0,
                "Room 208, Skin Clinic"
        ));

        doctors.add(new Doctor(
                "Dr. G. Ramesh, MD, DM",
                "Cardiology",
                "MBBS, MD (Internal Med), DM (Cardiology)",
                13,
                hospitals.get(3), // Kongu Demo Health (Coimbatore)
                "Tamil, English",
                "In-Person Consultation",
                650.0,
                "Room 310, Heart Centre"
        ));

        doctors.add(new Doctor(
                "Dr. A. Balamurugan, MD",
                "General Medicine",
                "MBBS, MD (Internal Medicine)",
                18,
                hospitals.get(4), // Demo Heritage Hospital (Madurai)
                "Tamil, English",
                "In-Person Consultation",
                400.0,
                "Room 105, General OPD"
        ));

        doctors.add(new Doctor(
                "Dr. D. Shanthi, MS, M.Ch",
                "Ophthalmology",
                "MBBS, MS (Ophthalmology), Cornea Specialist",
                10,
                hospitals.get(4), // Demo Heritage Hospital (Madurai)
                "Tamil, English",
                "In-Person Consultation",
                450.0,
                "Eye Care Unit, Room 2"
        ));

        doctors.add(new Doctor(
                "Dr. C. Vijay Anand, MD, DM",
                "Gastroenterology",
                "MBBS, MD (Medicine), DM (Medical Gastroenterology)",
                14,
                hospitals.get(5), // Kaveri Demo Health (Trichy)
                "Tamil, English",
                "In-Person Consultation",
                600.0,
                "Block C, Room 201"
        ));

        doctors.add(new Doctor(
                "Dr. P. Malarvizhi, MD",
                "General Medicine",
                "MBBS, MD (Internal Medicine)",
                10,
                hospitals.get(6), // Sample Kongu Healthcare (Salem)
                "Tamil, English",
                "In-Person Consultation",
                400.0,
                "OPD Room 1"
        ));

        doctors.add(new Doctor(
                "Dr. E. Murugesan, MD",
                "Pediatrics",
                "MBBS, MD (Pediatrics)",
                12,
                hospitals.get(7), // Demo Royal Care (Tirunelveli)
                "Tamil, English",
                "In-Person Consultation",
                450.0,
                "Pediatric Wing, Room 3"
        ));

        doctors.add(new Doctor(
                "Dr. J. Suresh, MS",
                "ENT",
                "MBBS, MS (ENT / Otorhinolaryngology)",
                9,
                hospitals.get(8), // Sample Fort Healthcare (Vellore)
                "Tamil, English",
                "In-Person Consultation",
                450.0,
                "ENT Clinic, Room 106"
        ));

        doctorRepository.saveAll(doctors);

        // 4. Generate Standard Availability Slots for all Doctors for next 7 days in batch
        LocalDate today = LocalDate.now();
        List<AvailabilitySlot> allSlots = new ArrayList<>();
        LocalTime[] slotTimes = new LocalTime[]{
                LocalTime.of(9, 0), LocalTime.of(9, 30),
                LocalTime.of(10, 0), LocalTime.of(10, 30),
                LocalTime.of(11, 0), LocalTime.of(11, 30),
                LocalTime.of(16, 0), LocalTime.of(16, 30),
                LocalTime.of(17, 0), LocalTime.of(17, 30)
        };
        for (Doctor doctor : doctors) {
            for (int d = 0; d < 7; d++) {
                LocalDate date = today.plusDays(d);
                for (LocalTime start : slotTimes) {
                    allSlots.add(new AvailabilitySlot(doctor, date, start, start.plusMinutes(30)));
                }
            }
        }
        slotRepository.saveAll(allSlots);

        // 5. Pre-seed a sample demo booking for the demo patient so the dashboard isn't completely blank
        // Pick one slot from Dr. K. Anitha for tomorrow
        List<AvailabilitySlot> anithaSlots = slotRepository.findByDoctorIdAndDateAndStatusOrderByStartTimeAsc(
                d1.getId(), today.plusDays(1), SlotStatus.AVAILABLE);

        if (!anithaSlots.isEmpty()) {
            AvailabilitySlot sampleSlot = anithaSlots.get(0);
            sampleSlot.setStatus(SlotStatus.BOOKED);
            slotRepository.save(sampleSlot);

            Appointment sampleAppointment = new Appointment(
                    "TNSH-" + today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-1001",
                    patientUser,
                    patientUser.getName(),
                    patientUser.getPhone(),
                    patientUser.getEmail(),
                    d1,
                    d1.getHospital(),
                    sampleSlot,
                    sampleSlot.getDate(),
                    sampleSlot.getStartTime(),
                    "Sample routine cardiology checkup (Demo Booking)"
            );
            appointmentRepository.save(sampleAppointment);

            notificationRepository.save(new Notification(
                    patientUser,
                    "Welcome to Tamil Nadu Smart Hospital demo! Sample appointment TNSH-1001 is ready for viewing in your dashboard.",
                    "SYSTEM"
            ));
        }

        System.out.println(">>> Demo seeding completed: " + hospitalRepository.count() + " hospitals, " +
                doctorRepository.count() + " doctors, " + slotRepository.count() + " slots created.");
    }
}

