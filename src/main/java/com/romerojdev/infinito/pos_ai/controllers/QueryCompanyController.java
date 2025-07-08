package com.romerojdev.infinito.pos_ai.controllers;

import com.romerojdev.infinito.pos_ai.model.Company;
import com.romerojdev.infinito.pos_ai.services.QueryCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mongoquery/companies")
public class QueryCompanyController {
    @Autowired
    private QueryCompanyService companyService;

    @GetMapping
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @PostMapping
    public ResponseEntity<Company> addCompany(@RequestBody Company company) {
        try {
            Company saved = companyService.saveIfNameNotExists(company);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
