package com.romerojdev.infinito.pos_ai.services;

import com.romerojdev.infinito.pos_ai.dto.ProductInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductInfoService {
    private final List<ProductInfoStrategy> strategies;

    @Autowired
    public ProductInfoService(List<ProductInfoStrategy> strategies) {
        this.strategies = strategies;
    }

    public ProductInfoDTO getProductInfo(String barcode) {
        // For now, use the first available strategy (OpenFoodFacts)
        if (!strategies.isEmpty()) {
            Object result = strategies.get(0).getProductInfo(barcode);
            if (result instanceof ProductInfoDTO) {
                return (ProductInfoDTO) result;
            }
        }
        return null;
    }
}
