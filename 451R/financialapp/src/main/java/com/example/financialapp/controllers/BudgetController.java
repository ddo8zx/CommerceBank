package com.example.financialapp.controllers;

import com.example.financialapp.models.BudgetSuggestion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BudgetController {

    @GetMapping("/budget")
    public String showBudgetForm(Model model) {
        model.addAttribute("suggestion", null);  // No data yet
        return "budget";
    }

    @PostMapping("/budget")
    public String generateBudget(@RequestParam double income, Model model) {
        double needs = income * 0.5;
        double wants = income * 0.3;
        double savings = income * 0.2;

        BudgetSuggestion suggestion = new BudgetSuggestion(needs, wants, savings);
        model.addAttribute("suggestion", suggestion);
        return "budget";
    }
}
