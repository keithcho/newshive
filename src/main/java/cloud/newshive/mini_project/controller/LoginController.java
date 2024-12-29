package cloud.newshive.mini_project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cloud.newshive.mini_project.model.User;
import cloud.newshive.mini_project.service.EmailService;
import cloud.newshive.mini_project.util.CodeGenerator;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class LoginController {

    @Autowired
    EmailService emailService;

    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    // Login page
    @GetMapping("/login")
    public String getLogin(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        
        if (user == null) {
            user = new User();
            session.setAttribute("user", user);
        }

        model.addAttribute("user", user);

        return "login";
    }

    // Endpoint for submission of login details
    @PostMapping(path="/login", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String handleLogin(@Valid @ModelAttribute User user, BindingResult binding,
                              HttpSession session, Model model) {

        if (binding.hasErrors()) {
            return "login";
        }

        logger.debug("Attempted verification check for " + user.getEmail());

        String verificationCode = CodeGenerator.generateCode();

        emailService.addVerificationCode(user.getEmail(), verificationCode);

        emailService.sendEmail(user.getEmail(), verificationCode);

        session.setAttribute("email", user.getEmail());
        session.setAttribute("isAuthenticated", false);
        
        return "redirect:/verify";
    }

    @GetMapping("/verify")
    public String showVerificationForm(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        if (email == null) {
            model.addAttribute("error", "Session expired. Please log in again.");
            return "login";
        }

        model.addAttribute("email", email);
        model.addAttribute("isAuthenticated", isAuthenticated);

        return "verify";
    }

    // Endpoint to verify login details
    @PostMapping(path="/verify")
    public String handleVerification(@RequestParam String verificationCode, HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");

        if (email == null) {
            model.addAttribute("errorMessage", "Session expired. Please log in again.");
            return "login";
        }

        String storedCode = emailService.getVerificationCode(email);

        if (storedCode != null && storedCode.equals(verificationCode)) {
            session.setAttribute("email", email);
            session.setAttribute("isAuthenticated", true); // Mark user as authenticated
            emailService.deleteVerificationCode(email);
            return "redirect:/top";
        } else {
            model.addAttribute("errorMessage", "Invalid or expired verification code.");
            model.addAttribute("email", email);
            return "verify";
        }
    }

    @GetMapping("/resend-code")
    public String resendCode(HttpSession session) {
        String email = (String) session.getAttribute("email");

        String storedCode = emailService.getVerificationCode(email);
        emailService.sendEmail(email, storedCode);

        return "redirect:/verify";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/top";
    }
}
