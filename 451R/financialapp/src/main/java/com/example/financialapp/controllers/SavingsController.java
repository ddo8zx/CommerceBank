package com.example.financialapp.controllers;

import com.example.financialapp.models.Expense;
import com.example.financialapp.models.SavingsGoal;
import com.example.financialapp.models.User;
import com.example.financialapp.repositories.ExpenseRepository;
import com.example.financialapp.repositories.SavingsGoalRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
public class SavingsController { // controls logic for savings/budget page

    @Autowired
    private ExpenseRepository expenseRepository; // connects to expense repository

    @Autowired
    private SavingsGoalRepository savingsGoalRepository; // connects to savings repository

    @GetMapping("/savings")
    public String showSavingsPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        SavingsGoal goal = savingsGoalRepository.findByUser(user);

        if (goal != null) {
            double monthlyBudget = goal.getMonthlyBudget();
            double[] budgetGoals = {
                    goal.getHousing(), goal.getFood(), goal.getTransportation(),
                    goal.getHealth(), goal.getPersonal(), goal.getOther()
            };
            YearMonth currentMonth = YearMonth.now();

            // calculate expenses
            List<Expense> expenses = expenseRepository.findAll().stream()
                    .filter(e -> e.getUser() != null && e.getUser().getId().equals(user.getId()))
                    .filter(e -> {
                        LocalDate date = e.getDate();
                        return date != null && YearMonth.from(date).equals(currentMonth);
                    })
                    .toList();

            double[] categorySpending = {
                    sumExpensesByCategory(expenses, "Housing/Utilities"),
                    sumExpensesByCategory(expenses, "Food/Grocery"),
                    sumExpensesByCategory(expenses, "Transportation"),
                    sumExpensesByCategory(expenses, "Health/Insurance/Debt"),
                    sumExpensesByCategory(expenses, "Personal/Entertainment"),
                    sumExpensesByCategory(expenses, "Other")
            };

            double[] budgetAmounts = new double[6];
            for (int i = 0; i < 6; i++) {
                budgetAmounts[i] = monthlyBudget * (budgetGoals[i] / 100.0);
            }

            model.addAttribute("monthlyBudget", monthlyBudget);
            model.addAttribute("budgetGoals", budgetGoals);
            model.addAttribute("budgetAmounts", budgetAmounts);
            model.addAttribute("categorySpending", categorySpending);
        }

        return "savings";
    }

    @PostMapping("/savings")
    public String saveSavingsGoals(@RequestParam double monthlyBudget,
                                   @RequestParam double housing,
                                   @RequestParam double food,
                                   @RequestParam double transportation,
                                   @RequestParam double health,
                                   @RequestParam double personal,
                                   @RequestParam double other,
                                   HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        SavingsGoal goal = savingsGoalRepository.findByUser(user);
        if (goal == null) {
            goal = new SavingsGoal();
            goal.setUser(user);
        }

        goal.setMonthlyBudget(monthlyBudget);
        goal.setHousing(housing);
        goal.setFood(food);
        goal.setTransportation(transportation);
        goal.setHealth(health);
        goal.setPersonal(personal);
        goal.setOther(other);

        savingsGoalRepository.save(goal);

        return "redirect:/savings";
    }

    private double sumExpensesByCategory(List<Expense> expenses, String category) {
        return expenses.stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}
