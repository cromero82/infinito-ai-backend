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

    public Company saveIfNameNotExists(Company company) {
        if (company.getName() == null) {
            throw new IllegalArgumentException("Company name must not be null");
        }
        return companyRepository.findByNameIgnoreCase(company.getName())
                .orElseGet(() -> {
                    if (company.getId() == null) {
                        // Assign next available Integer id
                        List<Company> all = companyRepository.findAll();
                        int nextId = all.stream()
                                .map(Company::getId)
                                .filter(java.util.Objects::nonNull)
                                .max(Integer::compareTo)
                                .orElse(0) + 1;
                        company.setId(nextId);
                    }
                    return companyRepository.save(company);
                });
    }
}
