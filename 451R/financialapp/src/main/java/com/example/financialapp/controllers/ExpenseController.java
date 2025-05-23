package com.example.financialapp.controllers;

import com.example.financialapp.models.Expense;
import com.example.financialapp.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.servlet.http.HttpSession;
import com.example.financialapp.models.User;

@Controller
public class ExpenseController { // controls logic from expense page

    @Autowired
    private ExpenseRepository expenseRepository; //connects to expense repository

    @GetMapping("/expenses")
    public String showExpenses(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Expense> expenses = expenseRepository.findAll()
                .stream()
                .filter(expense -> expense.getUser() != null && expense.getUser().getId().equals(loggedInUser.getId()))
                .toList();
        model.addAttribute("expenses", expenses);
        model.addAttribute("expense", new Expense());

        // calculates category counts
        Map<String, Long> categoryCounts = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.counting()));
        model.addAttribute("categoryCounts", categoryCounts);

        double totalAmount = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
        model.addAttribute("totalAmount", totalAmount);

        return "expenses";
    }

    @PostMapping("/expenses")
    public String addExpense(@ModelAttribute Expense expense, HttpSession session) {

        // use today's date if not provided
        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now());
        }
        // gets logged-in user from session
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // if somehow no user is found, redirect to login
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        // links the expense to the user
        expense.setUser(loggedInUser);

        expenseRepository.save(expense);
        return "redirect:/expenses";
    }

    @PostMapping("/expenses/delete/{id}") // allows deletion of expense
    public String deleteExpense(@PathVariable Long id) {
        expenseRepository.deleteById(id);
        return "redirect:/expenses";
    }
}
