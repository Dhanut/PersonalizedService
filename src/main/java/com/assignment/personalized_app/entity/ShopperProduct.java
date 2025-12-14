package com.assignment.personalized_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "shopper_product",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_shopper_product",
                        columnNames = {"shopper_id", "product_id"}
                )
        },
        indexes = {
                @Index(name = "idx_shopper_relevancy", columnList = "shopper_id, relevancy_score DESC")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopperProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shopper_id", nullable = false)
    private String shopperId;

    @Column(
            name = "relevancy_score",
            precision = 19,
            scale = 10
    )
    private BigDecimal relevancyScore;

    // Correct Many-to-One relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductMetadata product;
}
