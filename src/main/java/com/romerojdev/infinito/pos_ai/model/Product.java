package com.romerojdev.infinito.pos_ai.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String nombre;
    private List<String> tokens;
    private List<String> features;
    private Reference reference;
    private String type;
    private int price;
    private String photo;

    public static class Reference {
        private String barcode;
        private String company_id;
        private String marca;
        // getters and setters
        public String getBarcode() { return barcode; }
        public void setBarcode(String barcode) { this.barcode = barcode; }
        public String getCompany_id() { return company_id; }
        public void setCompany_id(String company_id) { this.company_id = company_id; }
        public String getMarca() { return marca; }
        public void setMarca(String marca) { this.marca = marca; }
    }
}
