package com.romerojdev.infinito.pos_ai.controllers;

import com.romerojdev.infinito.pos_ai.dto.ProductInfoDTO;
import com.romerojdev.infinito.pos_ai.services.ProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product-info")
public class ProductInfoController {
    @Autowired
    private ProductInfoService productInfoService;

    @GetMapping("/barcode/{barcode}")
    public ProductInfoDTO getProductInfoByBarcode(@PathVariable String barcode) {
        return productInfoService.getProductInfo(barcode);
    }
}
