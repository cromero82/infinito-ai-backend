package com.romerojdev.infinito.pos_ai.controllers;

import com.romerojdev.infinito.pos_ai.dto.ProductDTO;
import com.romerojdev.infinito.pos_ai.model.Product;
import com.romerojdev.infinito.pos_ai.services.IImageService;
import com.romerojdev.infinito.pos_ai.services.QueryProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/mongoquery/products")
public class QueryProductController {
    @Autowired
    private QueryProductService productService;

    @Autowired
    private IImageService imageService;

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

    @GetMapping("/paged-search")
    public Page<Product> pagedQueryByTokens(
            @RequestParam("q") String input,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.pagedQueryByTokens(input, pageable);
    }

    @GetMapping("/page-smart-search")
    public Page<Product> pageSmartSearchGrow(
            @RequestParam("q") String input,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.pageSmartSearchGrow(input, pageable);
    }

    @PostMapping("/add")
    public Product addProduct(@RequestBody @Valid ProductDTO dto,
                             @RequestParam(value = "imageUrl", required = false) String imageUrl) {
        return productService.addProduct(dto, imageUrl);
    }

    @PutMapping("/edit/{id}")
    public Product editProduct(@PathVariable String id,
                              @RequestBody @Valid ProductDTO dto,
                              @RequestParam(value = "imageUrl", required = false) String imageUrl) {
        return productService.editProduct(id, dto, imageUrl);
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
