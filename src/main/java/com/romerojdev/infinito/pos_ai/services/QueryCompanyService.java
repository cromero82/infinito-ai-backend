package com.romerojdev.infinito.pos_ai.services;

import com.romerojdev.infinito.pos_ai.model.Company;
import com.romerojdev.infinito.pos_ai.repository.QueryCompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryCompanyService {
    @Autowired
    private QueryCompanyRepository companyRepository;

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }
}

