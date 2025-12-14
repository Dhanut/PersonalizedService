package com.assignment.personalized_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "product_metadata")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductMetadata {

    @Id
    @Column(name = "product_id", nullable = false, unique = true)
    private String productId;

    @Column(name = "category")
    private String category;

    @Column(name = "brand")
    private String brand;

    // Bidirectional mapping (optional but correct)
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ShopperProduct> shopperProducts;
}

