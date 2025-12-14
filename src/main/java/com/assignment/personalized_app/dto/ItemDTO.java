package com.assignment.personalized_app.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ItemDTO {
    private String productId;
    private BigDecimal relevancyScore;
}
