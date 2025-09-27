package com.romerojdev.infinito.pos_ai.dto;

import lombok.Data;

@Data
public class ProductCreateDTO {
    private String barcode;
    private String nombre;
    private Double precio;
    private String foto;
    private Long companyId;
}

