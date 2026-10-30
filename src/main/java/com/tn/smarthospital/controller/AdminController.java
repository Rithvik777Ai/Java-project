package com.tn.smarthospital.controller;

import com.tn.smarthospital.dto.DoctorForm;
import com.tn.smarthospital.dto.HospitalForm;
import com.tn.smarthospital.dto.SmsResult;
import com.tn.smarthospital.model.Appointment;
import com.tn.smarthospital.model.AppointmentStatus;
import com.tn.smarthospital.model.Doctor;
import com.tn.smarthospital.model.Hospital;
import com.tn.smarthospital.model.User;
import com.tn.smarthospital.service.AppointmentService;
import com.tn.smarthospital.service.DoctorService;
import com.tn.smarthospital.service.HospitalService;
import com.tn.smarthospital.service.TextBeeSmsService;
import com.tn.smarthospital.service.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final HospitalService hospitalService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final UserService userService;
    private final TextBeeSmsService textBeeSmsService;

    public AdminController(HospitalService hospitalService,
                           DoctorService doctorService,
                           AppointmentService appointmentService,
                           UserService userService,
                           TextBeeSmsService textBeeSmsService) {
        this.hospitalService = hospitalService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.textBeeSmsService = textBeeSmsService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "date", required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                            @RequestParam(value = "hospitalId", required = false) Long hospitalId,
                            @RequestParam(value = "doctorId", required = false) Long doctorId,
                            @RequestParam(value = "status", required = false) AppointmentStatus status,
                            Model model) {
        List<Appointment> filteredAppointments = appointmentService.getAllAppointmentsAdmin(date, hospitalId, doctorId, status);
        List<Hospital> allHospitals = hospitalService.getAllHospitals();
        List<Doctor> allDoctors = doctorService.getAllDoctors();

        model.addAttribute("appointments", filteredAppointments);
        model.addAttribute("hospitals", allHospitals);
        model.addAttribute("doctors", allDoctors);
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedHospitalId", hospitalId);
        model.addAttribute("selectedDoctorId", doctorId);
        model.addAttribute("selectedStatus", status);

        // Stats
        model.addAttribute("totalHospitals", hospitalService.getTotalHospitalsCount());
        model.addAttribute("totalDoctors", doctorService.getTotalDoctorsCount());
        model.addAttribute("totalUsers", userService.getTotalUsersCount());
        model.addAttribute("stats", appointmentService.getSystemStatistics());
        model.addAttribute("smsService", textBeeSmsService);

        Optional<User> currentUser = userService.getCurrentUser();
        currentUser.ifPresent(user -> model.addAttribute("currentUser", user));

        return "admin/dashboard";
    }

    // Hospital Management
    @GetMapping("/hospitals/new")
    public String newHospitalForm(Model model) {
        HospitalForm form = new HospitalForm();
        model.addAttribute("hospitalForm", form);
        model.addAttribute("districts", hospitalService.getAllDistricts());
        return "admin/hospital-form";
    }

    @GetMapping("/hospitals/{id}/edit")
    public String editHospitalForm(@PathVariable("id") Long id, Model model) {
        Hospital hospital = hospitalService.getHospitalById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found: " + id));

        HospitalForm form = new HospitalForm();
        form.setId(hospital.getId());
        form.setName(hospital.getName());
        form.setHospitalType(hospital.getHospitalType());
        form.setState(hospital.getState());
        form.setDistrict(hospital.getDistrict());
        form.setCity(hospital.getCity());
        form.setAddress(hospital.getAddress());
        form.setPhone(hospital.getPhone());
        form.setEmail(hospital.getEmail());
        form.setDepartments(hospital.getDepartments());
        form.setDescription(hospital.getDescription());
        form.setActive(hospital.isActive());

        model.addAttribute("hospitalForm", form);
        model.addAttribute("districts", hospitalService.getAllDistricts());
        return "admin/hospital-form";
    }

    @PostMapping("/hospitals/save")
    public String saveHospital(@Valid @ModelAttribute("hospitalForm") HospitalForm hospitalForm,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("districts", hospitalService.getAllDistricts());
            return "admin/hospital-form";
        }

        hospitalService.saveHospital(hospitalForm);
        redirectAttributes.addFlashAttribute("successMessage", "Hospital record saved successfully (Demo Data).");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/hospitals/{id}/toggle")
    public String toggleHospital(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        hospitalService.toggleHospitalStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Hospital listing status updated.");
        return "redirect:/admin/dashboard";
    }

    // Doctor Management
    @GetMapping("/doctors/new")
    public String newDoctorForm(Model model) {
        DoctorForm form = new DoctorForm();
        model.addAttribute("doctorForm", form);
        model.addAttribute("hospitals", hospitalService.getAllActiveHospitals());
        return "admin/doctor-form";
    }

    @GetMapping("/doctors/{id}/edit")
    public String editDoctorForm(@PathVariable("id") Long id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + id));

        DoctorForm form = new DoctorForm();
        form.setId(doctor.getId());
        form.setName(doctor.getName());
        form.setSpecialty(doctor.getSpecialty());
        form.setQualification(doctor.getQualification());
        form.setExperienceYears(doctor.getExperienceYears());
        form.setHospitalId(doctor.getHospital().getId());
        form.setLanguages(doctor.getLanguages());
        form.setConsultationType(doctor.getConsultationType());
        form.setConsultationFee(doctor.getConsultationFee());
        form.setRoomNumber(doctor.getRoomNumber());
        form.setActive(doctor.isActive());

        model.addAttribute("doctorForm", form);
        model.addAttribute("hospitals", hospitalService.getAllActiveHospitals());
        return "admin/doctor-form";
    }

    @PostMapping("/doctors/save")
    public String saveDoctor(@Valid @ModelAttribute("doctorForm") DoctorForm doctorForm,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("hospitals", hospitalService.getAllActiveHospitals());
            return "admin/doctor-form";
        }

        Doctor savedDoctor = doctorService.saveDoctor(doctorForm);
        // If new doctor, generate initial slots
        if (doctorForm.getId() == null) {
            doctorService.generateStandardSlotsForDoctor(savedDoctor, LocalDate.now(), 7);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Doctor profile saved successfully (Demo Data).");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/doctors/{id}/toggle")
    public String toggleDoctor(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        doctorService.toggleDoctorStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Doctor listing status updated.");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/slots/generate")
    public String generateSlotsBatch(@RequestParam("doctorId") Long doctorId,
                                     @RequestParam(value = "days", defaultValue = "7") int days,
                                     RedirectAttributes redirectAttributes) {
        Doctor doctor = doctorService.getDoctorById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));

        int count = doctorService.generateStandardSlotsForDoctor(doctor, LocalDate.now(), days);
        redirectAttributes.addFlashAttribute("successMessage", "Created " + count + " new demo slots for " + doctor.getName());
        return "redirect:/admin/dashboard";
    }

    // TextBee SMS Test Endpoint
    @PostMapping("/sms/test")
    public String sendTestSms(@RequestParam("testPhone") String testPhone,
                             @RequestParam(value = "testMessage", required = false) String testMessage,
                             RedirectAttributes redirectAttributes) {
        if (testPhone == null || testPhone.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Test phone number is required to send a diagnostic SMS.");
            return "redirect:/admin/dashboard";
        }

        SmsResult result = textBeeSmsService.sendTestSms(testPhone.trim(), testMessage);
        if (result.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Test SMS successfully dispatched! " + result.getMessage());
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Test SMS delivery failed: " + result.getMessage());
        }

        return "redirect:/admin/dashboard";
    }
}

