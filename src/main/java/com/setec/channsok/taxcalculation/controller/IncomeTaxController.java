package com.setec.channsok.taxcalculation.controller;
import com.setec.channsok.taxcalculation.dto.IncomeTexRequest;
import com.setec.channsok.taxcalculation.dto.IncomeTexResponse;
import com.setec.channsok.taxcalculation.service.IncomeTaxService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/tax")
public class IncomeTaxController {
    @Autowired
    private  IncomeTaxService incomeTaxService;
    
    @PostMapping("/calculate")
    public ResponseEntity<IncomeTexResponse> calculateIncomeTax( @RequestBody IncomeTexRequest request) {
        IncomeTexResponse response = incomeTaxService.calculatingIncomeTax(request);
        return ResponseEntity.ok(response);
    }
//    @GetMapping("/calculation/get")
//    public ResponseEntity<IncomeTexResponse> getAllCalculation(@PathVariable IncomeTexRequest request, Long id){
//
//    }
}