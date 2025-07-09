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
    public ResponseEntity<Type> getTypeById(@PathVariable Integer id) {
        Optional<Type> type = queryTypesService.findById(id);
        return type.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Type createType(@RequestBody Type type) {

        return queryTypesService.saveWithTranslation(type);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Type> updateType(@PathVariable Integer id, @RequestBody Type typeDetails) {
        Optional<Type> optionalType = queryTypesService.findById(id);
        if (!optionalType.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Type type = optionalType.get();
        return ResponseEntity.ok(queryTypesService.updateWithTranslation(type, typeDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable Integer id) {
        if (!queryTypesService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        queryTypesService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
