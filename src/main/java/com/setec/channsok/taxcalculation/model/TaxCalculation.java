package com.setec.channsok.taxcalculation.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_tax_calculator")
public class TaxCalculation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal salary;
    private double exchangeRate;
    private String currency;
    private boolean resident;
    private BigDecimal fringeBenefits ;
    private boolean spouse;
    private int totalDependents;
    private BigDecimal taxAmount;
    private BigDecimal taxRate;
    private BigDecimal netSalary;
    private LocalDateTime createdAt;
}
