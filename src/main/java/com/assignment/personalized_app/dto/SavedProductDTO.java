package com.assignment.personalized_app.dto;

import lombok.AllArgsConstructor;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedProductDTO {
    private Long id;
    private String shopperId;
    private BigDecimal relevancyScore;
    private String productId;
    private String category;
    private String brand;
}