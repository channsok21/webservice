package com.setec.channsok.taxcalculation.service;


import com.setec.channsok.taxcalculation.dto.IncomeTexRequest;
import com.setec.channsok.taxcalculation.dto.IncomeTexResponse;
import com.setec.channsok.taxcalculation.model.TaxCalculation;
import com.setec.channsok.taxcalculation.repository.TaxCalculationRepo;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor

public class IncomeTaxService {
    private final TaxCalculationRepo taxCalculationRepo;

    public IncomeTexResponse calculatingIncomeTax(IncomeTexRequest request) {
        //  Convert all amounts to KHR (common base currency)
        BigDecimal salaryKhr = convertToKhr(request.getSalary(), request.getCurrency(), request.getExchangeRate());
        BigDecimal fringeBenefitsKhr = convertToKhr(request.getFringeBenefits(), request.getCurrency(), request.getExchangeRate());
        
        //  Calculate tax on salary
        IncomeTexResponse response;
        if(request.isResident()){
            response=calculateResidentTax(salaryKhr);
        }
        else {
            response=calculateResidentTax(salaryKhr);
        }

        //  Calculate tax on fringe benefits
        calculateFringeBenefitsTax(response, fringeBenefitsKhr, request.getExchangeRate());
        
        // Calculate totals and convert to USD
        calculateTotals(response, request.getExchangeRate());
        
        log.info("Tax calculation completed: {}", response);



        this.saveDataToDatabase(request,response);


        return response;
    }
    private IncomeTexResponse saveDataToDatabase(IncomeTexRequest request, IncomeTexResponse response){
        TaxCalculation record=new TaxCalculation();


        record.setSalary(request.getSalary());
        record.setExchangeRate(request.getExchangeRate());
        record.setCurrency(request.getCurrency());
        record.setResident(request.isResident());
        record.setFringeBenefits(request.getFringeBenefits());
        record.setSpouse(request.isSpouse());
        record.setTotalDependents(request.getTotalDependents());
        record.setTaxAmount(response.getTaxOnSalaryKhr());
        record.setTaxRate(response.getTaxRate());
        record.setNetSalary(response.getNetSalaryInKhr());
        record.setCreatedAt(LocalDateTime.now(ZoneId.of("GMT+7")));




        TaxCalculation saveRecord=taxCalculationRepo.save(record);
        response.setSalary(saveRecord.getSalary());

        return response;

    }
    
    /**
     * Converts amount to KHR if it's in USD
     */
    private BigDecimal convertToKhr(BigDecimal amount, String currency, double exchangeRate) {
        if (amount == null) return BigDecimal.ZERO;
        return "USD".equalsIgnoreCase(currency) 
            ? amount.multiply(BigDecimal.valueOf(exchangeRate))
            : amount;
    }
    /**
     * Calculates tax for resident taxpayers using progressive rates
     */
    private IncomeTexResponse calculateResidentTax(BigDecimal monthlySalaryKhr){
        IncomeTexResponse response= new IncomeTexResponse();
        BigDecimal taxAmount;

        if (monthlySalaryKhr.compareTo(new BigDecimal("1300000")) <= 0) {
            // 0% tax for income ≤ 1,300,000 KHR
            taxAmount = BigDecimal.ZERO;
            response.setTaxRate(BigDecimal.ZERO);
        }
        else if (monthlySalaryKhr.compareTo(new BigDecimal("2000000")) <= 0) {
            // Fixed 65,000 KHR for 5% bracket
            taxAmount = new BigDecimal("65000");
            response.setTaxRate(new BigDecimal("5"));
        }
        else if (monthlySalaryKhr.compareTo(new BigDecimal("8500000")) <= 0) {
            // Fixed 165,000 KHR for 10% bracket
            taxAmount = new BigDecimal("165000");
            response.setTaxRate(new BigDecimal("10"));
        }
        else if (monthlySalaryKhr.compareTo(new BigDecimal("12500000")) <= 0) {
            // Fixed 590,000 KHR for 15% bracket
            taxAmount = new BigDecimal("590000");
            response.setTaxRate(new BigDecimal("15"));
        }
        else {
            // Fixed 1,250,000 KHR for 20% bracket
            taxAmount = new BigDecimal("1250000");
            response.setTaxRate(new BigDecimal("20"));
        }

        // Calculate net salary
        BigDecimal netSalary = monthlySalaryKhr.subtract(taxAmount);
        response.setNetSalaryInKhr(netSalary);
        response.setTaxOnSalaryKhr(taxAmount);

        return response;
    }
    


    /**
     * Calculates flat tax for non-resident taxpayers
     */
    private IncomeTexResponse calculateNonResidentTax(BigDecimal salaryKhr) {
        IncomeTexResponse response = new IncomeTexResponse();
        BigDecimal taxAmount = salaryKhr.multiply(BigDecimal.valueOf(0.20));
        
        response.setNetSalaryInKhr(salaryKhr.subtract(taxAmount));
        response.setTaxOnSalaryKhr(taxAmount);
        response.setTaxRate(BigDecimal.valueOf(0.20).multiply(BigDecimal.valueOf(100)));
        
        return response;
    }
    
    /**
     * Calculates tax on fringe benefits (20% flat rate)
     */
    private void calculateFringeBenefitsTax(IncomeTexResponse response, BigDecimal fringeBenefitsKhr, double exchangeRate) {
        if (fringeBenefitsKhr.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal fringeTax = fringeBenefitsKhr.multiply(BigDecimal.valueOf(0.20));
            BigDecimal netFringeBenefits = fringeBenefitsKhr.subtract(fringeTax);
            
            response.setTaxOnFringeBenefitsKhr(fringeTax);
            response.setTaxOnFringeBenefitsUsd(
                convertToUsd(fringeTax, exchangeRate)
            );
            
            // Add net benefits to net salary
            response.setNetSalaryInKhr(
                response.getNetSalaryInKhr().add(netFringeBenefits)
            );
        }
    }
    
    /**
     * Calculates totals and converts amounts to USD
     */
    private void calculateTotals(IncomeTexResponse response, double exchangeRate) {
        // Convert KHR amounts to USD
        response.setNetSalaryInUsd(convertToUsd(response.getNetSalaryInKhr(), exchangeRate));
        response.setTaxOnSalaryUsd(convertToUsd(response.getTaxOnSalaryKhr(), exchangeRate));

        // Calculate total tax (salary + fringe benefits)
        BigDecimal totalTaxKhr ;BigDecimal salaryTax = response.getTaxOnSalaryKhr();

        if (response.getTaxOnFringeBenefitsKhr() != null) {
            totalTaxKhr = salaryTax.add(response.getTaxOnFringeBenefitsKhr());
        } else {
            totalTaxKhr = salaryTax.add(BigDecimal.ZERO);
        }
        response.setTotalTaxUsd(
            convertToUsd(totalTaxKhr, exchangeRate)
        );
    }
    
    /**
     * Converts KHR to USD
     */
    private BigDecimal convertToUsd(BigDecimal amountKhr, double exchangeRate) {
        return amountKhr.divide(
            BigDecimal.valueOf(exchangeRate),
            2,
            RoundingMode.HALF_UP
        );
    }
}