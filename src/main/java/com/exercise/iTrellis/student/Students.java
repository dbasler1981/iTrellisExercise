package com.exercise.iTrellis.student;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Students
{
    private String name;
    private int id;
    // private List<Expense> expense;
    // Using instead of Expense
    private String meals;
    private String hotels;
    private String taxi;
    private String planeTickets;
    // Using instead of Expense
    
    private BigDecimal totalCost;
    private BigDecimal remainingCash;
}