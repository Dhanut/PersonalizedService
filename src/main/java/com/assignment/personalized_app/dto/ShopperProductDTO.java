package com.assignment.personalized_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopperProductDTO {
    private String productId;
    private BigDecimal relevancyScore;
    private String category;
    private String brand;
}
