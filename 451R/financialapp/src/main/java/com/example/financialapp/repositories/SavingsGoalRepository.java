package com.example.financialapp.repositories;

import com.example.financialapp.models.SavingsGoal;
import com.example.financialapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    SavingsGoal findByUser(User user);
}
