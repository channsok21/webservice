package com.setec.channsok.taxcalculation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class IncomeTexResponse {
    @JsonProperty("net_salary_usd")
    private BigDecimal netSalaryInUsd;
    @JsonProperty("net_salary_khr")
    private BigDecimal netSalaryInKhr;
    @JsonProperty("tax_on_salary_usd")
    private BigDecimal taxOnSalaryUsd;
    @JsonProperty("tax_on_salary_khr")
    private BigDecimal taxOnSalaryKhr;
    @JsonProperty("tax_on_fringe_benefits_usd")
    private BigDecimal taxOnFringeBenefitsUsd;
    @JsonProperty("tax_on_fringe_benefits_khr")
    private BigDecimal taxOnFringeBenefitsKhr;
    @JsonProperty("total_tax_usd")
    private BigDecimal totalTaxUsd;
    @JsonProperty("tax_rate")
    private BigDecimal taxRate;

    @JsonProperty("created_at")
    private LocalDateTime created_At;
    @JsonProperty("salary")
    private BigDecimal salary;




}