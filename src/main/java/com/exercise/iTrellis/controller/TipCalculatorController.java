package com.exercise.iTrellis.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.exercise.iTrellis.student.JSONRoot;
import com.exercise.iTrellis.student.Owes;
import com.exercise.iTrellis.student.Students;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TipCalculatorController {

  @GetMapping("/")
  public String home() {
    return "home";
  }

  @PostMapping(
    path = "/calculate",
    consumes = "application/json",
    produces = "application/json"
  )
  public List<Owes> calculateExpenseForStudents(
    @RequestBody JSONRoot jsonRoot
  ) {
    List<Owes> owesList = new ArrayList<Owes>();
    BigDecimal total = new BigDecimal("0.0");
    BigDecimal divisor = new BigDecimal(jsonRoot.getStudents().size());

    for (Students student : jsonRoot.getStudents()) {
      BigDecimal cost = new BigDecimal("0.0");
      
      // for (Expense expense : student.getExpense()) {
        if (student.getHotels() != null) {
          cost = cost.add(new BigDecimal(student.getHotels()));
        }

        if (student.getMeals() != null) {
          cost = cost.add(new BigDecimal(student.getMeals()));
        }

        if (student.getPlaneTickets() != null) {
          cost = cost.add(new BigDecimal(student.getPlaneTickets()));
        }

        if (student.getTaxi() != null) {
          cost = cost.add(new BigDecimal(student.getTaxi()));
        }
        student.setTotalCost(cost);
      // }
    }

    for (Students student : jsonRoot.getStudents()) {
      total = total.add(student.getTotalCost());
    }

    total = total.divide(divisor, RoundingMode.DOWN);

    // Sort by who has the most money
    Collections.sort(
      jsonRoot.getStudents(),
      new Comparator<Students>() {

        @Override
        public int compare(Students a, Students b) {
          return b.getTotalCost().compareTo(a.getTotalCost());
        }
      }
    );

    // Now subtract the average cost per person.
    // If someone has remaining money, they can borrow to others.
    //
    for (Students student : jsonRoot.getStudents()) {
      System.out.printf("%s %s\n", student.getName(), student.getTotalCost());
      student.setRemainingCash(student.getTotalCost().subtract(total));
      Owes owes = borrowFromOthers(student, jsonRoot.getStudents(), total);
      owesList.add(owes);
    }

    return owesList;
  }

  private Owes borrowFromOthers(
    Students currentStudent,
    List<Students> students,
    BigDecimal total
  ) {
    Owes owes = new Owes();
    owes.setName(currentStudent.getName());
    if (currentStudent.getRemainingCash().compareTo(BigDecimal.ZERO) > 0) {
      owes.setOwesTo("");
      owes.setAmount(new BigDecimal("0.00"));
      return owes;
    }

    for (Students student : students) {
      if (student.getId() == currentStudent.getId()) {
        continue;
      }
      // This student has cash to borrow.  We've sorted by the students with the most cash to borrow first.
      if (student.getRemainingCash().compareTo(BigDecimal.ZERO) > 0) {
        owes.setOwesTo(student.getName());
        owes.setAmount(currentStudent.getRemainingCash().negate());
        
        BigDecimal subtractedCash = student.getRemainingCash().subtract(owes.getAmount());
        student.setRemainingCash(subtractedCash);
        break;
      }
    }
    return owes;
  }
}
