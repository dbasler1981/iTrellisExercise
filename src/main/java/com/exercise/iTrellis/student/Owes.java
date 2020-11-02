package com.exercise.iTrellis.student;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Owes {
    String name;
    String owesTo;
    BigDecimal amount;
}