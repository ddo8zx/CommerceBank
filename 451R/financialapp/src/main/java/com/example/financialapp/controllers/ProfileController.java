package com.example.financialapp.controllers;

import com.example.financialapp.models.User;
import com.example.financialapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public String showProfile(Model model) {
        // Simulate a logged-in user — in the real world, you'd use sessions
        User user = userRepository.findAll().stream().findFirst().orElse(null);

        model.addAttribute("user", user);
        return "profile";
    }
}
