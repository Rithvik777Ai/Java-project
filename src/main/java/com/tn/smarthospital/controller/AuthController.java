package com.tn.smarthospital.controller;

import com.tn.smarthospital.dto.RegisterForm;
import com.tn.smarthospital.model.User;
import com.tn.smarthospital.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Invalid email or password. Please try again or use the 1-Click Demo Login.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
        }

        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            return "redirect:/dashboard-redirect";
        }

        return "auth/login";
    }

    @GetMapping("/demo-login")
    public String demoQuickLogin(@RequestParam("role") String role,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes) {
        String email;
        if ("ADMIN".equalsIgnoreCase(role)) {
            email = "admin@demo.local";
        } else if ("DOCTOR".equalsIgnoreCase(role)) {
            email = "doctor@demo.local";
        } else {
            email = "patient@demo.tn.gov.in";
        }

        UserDetails userDetails = userService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        redirectAttributes.addFlashAttribute("successMessage",
                "Logged in as Demo " + role.toUpperCase() + " (" + email + "). Welcome!");

        return "redirect:/dashboard-redirect";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            return "redirect:/dashboard-redirect";
        }

        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("registerForm") RegisterForm registerForm,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (!registerForm.getPassword().equals(registerForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registerForm", "Passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerPatient(registerForm);
            redirectAttributes.addFlashAttribute("successMessage", "Demo registration successful! Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("registrationError", ex.getMessage());
            return "auth/register";
        }
    }
}

