package com.setec.channsok.taxcalculation.repository;

import com.setec.channsok.taxcalculation.model.TaxCalculation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaxCalculationRepo extends JpaRepository<TaxCalculation, Long> {
}
