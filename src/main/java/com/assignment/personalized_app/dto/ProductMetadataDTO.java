package com.assignment.personalized_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMetadataDTO {
    private String productId;
    private String category;
    private String brand;
}
