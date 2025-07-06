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

    @Autowired
    private TranslationService translationService;

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

    public Type saveWithTranslation(Type type) {
        if (type.getName() != null && translationService.isEnglish(type.getName())) {
            type.setName(translationService.translateEnToEs(type.getName()));
        }
        // Check if a type with the translated name already exists
        Optional<Type> existing = queryTypesRepository.findAll().stream()
            .filter(t -> t.getName().equalsIgnoreCase(type.getName()))
            .findFirst();
        if (existing.isPresent()) {
            return existing.get();
        }
        return queryTypesRepository.save(type);
    }

    public Type updateWithTranslation(Type existing, Type updates) {
        String name = updates.getName();
        if (name != null && translationService.isEnglish(name)) {
            name = translationService.translateEnToEs(name);
        }
        // Check if a type with the translated name already exists
        String finalName = name;
        Optional<Type> found = queryTypesRepository.findAll().stream()
            .filter(t -> t.getName().equalsIgnoreCase(finalName))
            .findFirst();
        if (found.isPresent()) {
            // If percentProfit is provided, update it
            if (updates.getPercentProfit() != null) {
                Type foundType = found.get();
                foundType.setPercentProfit(updates.getPercentProfit());
                return queryTypesRepository.save(foundType);
            }
            return found.get();
        }
        existing.setName(name);
        existing.setPercentProfit(updates.getPercentProfit());
        return queryTypesRepository.save(existing);
    }
}
