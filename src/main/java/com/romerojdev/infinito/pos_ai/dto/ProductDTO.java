package com.romerojdev.infinito.pos_ai.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class ProductDTO {
    @NotBlank
    private String nombre;
    private ReferenceDTO reference;
    private String type;
    @NotNull
    private Integer price;
    private String photo;
    private String imageUrl; // Optional field for direct image URL

    @Data
    public static class ReferenceDTO {
        private String barcode;
        @JsonProperty("company_id")
        private Integer companyId;
    }
}
