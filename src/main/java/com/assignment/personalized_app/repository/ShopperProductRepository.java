package com.assignment.personalized_app.repository;

import com.assignment.personalized_app.entity.ShopperProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ShopperProductRepository extends JpaRepository<ShopperProduct, Long> {

    @Query("""
        SELECT sp FROM ShopperProduct sp
        JOIN sp.product p
        WHERE sp.shopperId = :shopperId
          AND (:category IS NULL OR p.category = :category)
          AND (:brand IS NULL OR p.brand = :brand)
        ORDER BY sp.relevancyScore DESC
        """)
    Page<ShopperProduct> findByShopperWithFilters(
            @Param("shopperId") String shopperId,
            @Param("category") String category,
            @Param("brand") String brand,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE ShopperProduct sp SET sp.relevancyScore = :score WHERE sp.shopperId = :shopperId AND sp.product.productId = :productId")
    void updateRelevancyScore(@Param("shopperId") String shopperId,
                              @Param("productId") String productId,
                              @Param("score") BigDecimal score);

    @Query("""
            SELECT sp.product.productId, sp.relevancyScore 
            FROM ShopperProduct sp 
            WHERE sp.shopperId = :shopperId AND sp.product.productId IN :productIds
            """)
    Optional<List<Object[]>> findExistingForShopper(@Param("shopperId") String shopperId,
                                                    @Param("productIds") List<String> productIds);

    List<ShopperProduct> findByShopperId(String shopperId);
}
