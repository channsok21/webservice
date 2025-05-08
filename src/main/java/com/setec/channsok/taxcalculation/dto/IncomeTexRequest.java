package com.setec.channsok.taxcalculation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class IncomeTexRequest {
    @JsonProperty("salary")
    @NotNull(message = "Salary cannot be null")
    @PositiveOrZero(message = "Salary must be positive or zero")
    private BigDecimal salary;
    
    @JsonProperty("exchange_rate")
    @Positive(message = "Exchange rate must be positive")
    private double exchangeRate;
    
    @JsonProperty("currency")
    @NotBlank(message = "Currency cannot be blank")
    @Pattern(regexp = "USD|KHR", flags = Pattern.Flag.CASE_INSENSITIVE,
             message = "Currency must be either USD or KHR")
    private String currency;
    
    @JsonProperty("resident")
    private boolean resident;
    
    @JsonProperty("fringe_benefits")
    @PositiveOrZero(message = "Fringe benefits must be positive or zero")
    private BigDecimal fringeBenefits = BigDecimal.ZERO;
    
    @JsonProperty("spouse")
    private boolean spouse;
    
    @JsonProperty("total_dependents")
    @Min(value = 0, message = "Dependents cannot be negative")
    @Max(value = 10, message = "Maximum 10 dependents allowed")
    private int totalDependents = 0;

    @JsonProperty("created_at")
    private LocalDateTime created_At;
}