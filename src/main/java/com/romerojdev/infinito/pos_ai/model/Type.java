package com.romerojdev.infinito.pos_ai.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "types")
public class Type {
    @Id
    private Integer id;
    private String name;
    private Double percentProfit;

    public Type() {}

    public Type(Integer id, String name, Double percentProfit) {
        this.id = id;
        this.name = name;
        this.percentProfit = percentProfit;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPercentProfit() {
        return percentProfit;
    }

    public void setPercentProfit(Double percentProfit) {
        this.percentProfit = percentProfit;
    }
}
