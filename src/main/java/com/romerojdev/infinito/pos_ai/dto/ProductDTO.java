package com.romerojdev.infinito.pos_ai.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class ProductDTO {
    @NotBlank
    private String nombre;
    @NotNull
    private ReferenceDTO reference;
    @NotBlank
    private String type;
    @NotNull
    private Integer price;
    private String photo;

    @Data
    public static class ReferenceDTO {
        @NotBlank
        private String barcode;
        @NotNull
        @JsonProperty("company_id")
        private Integer companyId;
    }
}
