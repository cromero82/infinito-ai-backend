package com.romerojdev.infinito.pos_ai.services;

import com.romerojdev.infinito.pos_ai.entities.Product;
import com.romerojdev.infinito.pos_ai.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> getAll(String barcodeOrName, Pageable pageable) {
        if (barcodeOrName != null && !barcodeOrName.isEmpty()) {
            return productRepository.findByBarcodeContainingOrNombreContaining(barcodeOrName, barcodeOrName, pageable);
        }
        return productRepository.findAll(pageable);
    }
}
