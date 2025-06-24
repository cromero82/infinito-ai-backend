package com.romerojdev.infinito.pos_ai.controllers;

import com.romerojdev.infinito.pos_ai.model.Product;
import com.romerojdev.infinito.pos_ai.services.QueryProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mongoquery/products")
public class QueryProductController {
    @Autowired
    private QueryProductService productService;

    @GetMapping("/search")
    public List<Product> queryByTokens(@RequestParam("q") String input) {
        return productService.queryByTokens(input);
    }

    @GetMapping("/smart-search")
    public List<Product> smartSearch(@RequestParam("q") String input) {
        return productService.queryByTokensSmart(input);
    }

    @GetMapping("/smart-search-grow")
    public List<Product> smartSearchGrow(@RequestParam("q") String input) {
        return productService.smartSearchGrow(input);
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
