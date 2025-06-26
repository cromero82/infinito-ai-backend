package com.romerojdev.infinito.pos_ai.service;

import com.romerojdev.infinito.pos_ai.model.Type;
import com.romerojdev.infinito.pos_ai.repository.QueryTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QueryTypesService {
    @Autowired
    private QueryTypesRepository queryTypesRepository;

    public List<Type> findAll() {
        return queryTypesRepository.findAll();
    }

    public Optional<Type> findById(String id) {
        return queryTypesRepository.findById(id);
    }

    public Type save(Type type) {
        return queryTypesRepository.save(type);
    }

    public void deleteById(String id) {
        queryTypesRepository.deleteById(id);
    }
}

