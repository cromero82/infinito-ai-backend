package com.romerojdev.infinito.pos_ai.controller;

import com.romerojdev.infinito.pos_ai.model.Type;
import com.romerojdev.infinito.pos_ai.service.QueryTypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/types")
public class QueryTypesController {
    @Autowired
    private QueryTypesService queryTypesService;

    @GetMapping
    public List<Type> getAllTypes() {
        return queryTypesService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Type> getTypeById(@PathVariable String id) {
        Optional<Type> type = queryTypesService.findById(id);
        return type.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Type createType(@RequestBody Type type) {
        return queryTypesService.save(type);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Type> updateType(@PathVariable String id, @RequestBody Type typeDetails) {
        Optional<Type> optionalType = queryTypesService.findById(id);
        if (!optionalType.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Type type = optionalType.get();
        type.setName(typeDetails.getName());
        type.setPercentProfit(typeDetails.getPercentProfit());
        return ResponseEntity.ok(queryTypesService.save(type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable String id) {
        if (!queryTypesService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        queryTypesService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

