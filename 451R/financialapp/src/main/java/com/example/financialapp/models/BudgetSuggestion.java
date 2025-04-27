package com.example.financialapp.models;

public class BudgetSuggestion {
    private double needs;
    private double wants;
    private double savings;

    public BudgetSuggestion(double needs, double wants, double savings) {
        this.needs = needs;
        this.wants = wants;
        this.savings = savings;
    }

    public double getNeeds() {
        return needs;
    }

    public double getWants() {
        return wants;
    }

    public double getSavings() {
        return savings;
    }
}
