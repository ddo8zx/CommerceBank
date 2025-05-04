package com.example.financialapp.controllers;

import com.example.financialapp.models.User;
import com.example.financialapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController { // controls user logic

    @Autowired
    private UserRepository userRepository; // connects to user repository

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Username already exists.");
            return "register";
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already registered.");
            return "register";
        }

        userRepository.save(user);  // Plaintext for now, will hash later
        model.addAttribute("message", "Registration successful! Please log in.");
        return "login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            Model model,
                            HttpSession session) {

        User user = userRepository.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }

        // save user in session for later pages
        session.setAttribute("loggedInUser", user);

        model.addAttribute("username", user.getUsername());
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", loggedInUser.getUsername());
        return "dashboard";
    }

    @GetMapping("/recover")
    public String showRecoveryPage(Model model) {
        model.addAttribute("userFound", false);
        return "recover";
    }

    @PostMapping("/recover")
    public String handleRecovery(@RequestParam String email,
                                 @RequestParam(required = false) String securityAnswer,
                                 Model model) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            model.addAttribute("error", "No user found with that email.");
            model.addAttribute("userFound", false);
            return "recover";
        }

        if (securityAnswer == null) {
            // phase 1: show security question
            model.addAttribute("email", email);
            model.addAttribute("securityQuestion", user.getSecurityQuestion());
            model.addAttribute("userFound", true);
            return "recover";
        }

        if (user.getSecurityAnswer().equalsIgnoreCase(securityAnswer.trim())) {
            // phase 2: correct answer, show credentials
            model.addAttribute("recoveredUser", user);
        } else {
            model.addAttribute("error", "Incorrect answer. Please try again.");
        }

        model.addAttribute("userFound", true);
        model.addAttribute("securityQuestion", user.getSecurityQuestion());
        model.addAttribute("email", email);
        return "recover";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // kill the session
        return "redirect:/login";  // send back to login page
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session,
                                 Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        // check if the current password matches
        if (!loggedInUser.getPassword().equals(currentPassword)) {
            model.addAttribute("error", "Current password is incorrect.");
            model.addAttribute("user", loggedInUser);
            return "profile";
        }

        // update the password
        loggedInUser.setPassword(newPassword);
        userRepository.save(loggedInUser);

        model.addAttribute("message", "Password updated successfully.");
        model.addAttribute("user", loggedInUser);
        return "profile";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", loggedInUser);
        return "profile";
    }
}


