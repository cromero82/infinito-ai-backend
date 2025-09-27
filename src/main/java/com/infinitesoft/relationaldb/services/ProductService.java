package com.infinitesoft.relationaldb.services;

import com.infinitesoft.relationaldb.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<Product> getAll(String barcodeOrName, Pageable pageable);
}
