package com.example.financialapp.models;

import jakarta.persistence.*;

@Entity
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double monthlyBudget;
    private double housing;
    private double food;
    private double transportation;
    private double health;
    private double personal;
    private double other;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public double getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(double monthlyBudget) { this.monthlyBudget = monthlyBudget; }

    public double getHousing() { return housing; }
    public void setHousing(double housing) { this.housing = housing; }

    public double getFood() { return food; }
    public void setFood(double food) { this.food = food; }

    public double getTransportation() { return transportation; }
    public void setTransportation(double transportation) { this.transportation = transportation; }

    public double getHealth() { return health; }
    public void setHealth(double health) { this.health = health; }

    public double getPersonal() { return personal; }
    public void setPersonal(double personal) { this.personal = personal; }

    public double getOther() { return other; }
    public void setOther(double other) { this.other = other; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
