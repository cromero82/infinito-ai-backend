package com.romerojdev.infinito.pos_ai.controllers;

import com.romerojdev.infinito.pos_ai.model.Company;
import com.romerojdev.infinito.pos_ai.services.QueryCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

