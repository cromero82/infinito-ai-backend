package com.romerojdev.infinito.pos_ai.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String nombre;
    private List<String> tokens;
    private List<String> features;
    private Reference reference;

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<String> getTokens() { return tokens; }
    public void setTokens(List<String> tokens) { this.tokens = tokens; }
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    public Reference getReference() { return reference; }
    public void setReference(Reference reference) { this.reference = reference; }

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

