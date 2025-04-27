package com.example.financialapp.repositories;

import com.example.financialapp.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    // We can add custom queries here later if needed
}
