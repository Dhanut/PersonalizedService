package com.assignment.personalized_app.repository;

import com.assignment.personalized_app.entity.ProductMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductMetadataRepository extends JpaRepository<ProductMetadata, String> {
    @Query("SELECT pm.productId FROM ProductMetadata pm WHERE pm.productId IN :ids")
    Optional<List<String>> findExistingProductIds(@Param("ids") List<String> ids);

}
