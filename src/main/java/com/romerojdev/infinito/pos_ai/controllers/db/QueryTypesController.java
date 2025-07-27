package com.romerojdev.infinito.pos_ai.controllers.db;

import com.romerojdev.infinito.pos_ai.model.Type;
import com.romerojdev.infinito.pos_ai.service.QueryTypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/types")
public class QueryTypesController {
    private static final Logger logger = LoggerFactory.getLogger(QueryTypesController.class);

    @Autowired
    private QueryTypesService queryTypesService;

    @GetMapping
    public List<Type> getAllTypes() {
        logger.info("GET /api/types called");
        return queryTypesService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Type> getTypeById(@PathVariable Integer id) {
        logger.info("GET /api/types/{} called", id);
        Optional<Type> type = queryTypesService.findById(id);
        return type.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Type createType(@RequestBody Type type) {
        logger.info("POST /api/types called with body: {}", type);
        return queryTypesService.saveWithTranslation(type);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Type> updateType(@PathVariable Integer id, @RequestBody Type typeDetails) {
        logger.info("PUT /api/types/{} called with body: {}", id, typeDetails);
        Optional<Type> optionalType = queryTypesService.findById(id);
        if (!optionalType.isPresent()) {
            logger.warn("Type with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        Type type = optionalType.get();
        return ResponseEntity.ok(queryTypesService.updateWithTranslation(type, typeDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable Integer id) {
        logger.info("DELETE /api/types/{} called", id);
        if (!queryTypesService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        queryTypesService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
