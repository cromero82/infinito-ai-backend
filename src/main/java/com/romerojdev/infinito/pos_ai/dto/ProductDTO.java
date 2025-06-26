package com.romerojdev.infinito.pos_ai.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ProductDTO {
    @NotBlank
    private String nombre;

    private String barcode;
    @NotNull
    private Integer companyId;
    @NotBlank
    private String type;
    @NotNull
    private Integer price;
    private String photo;
}
