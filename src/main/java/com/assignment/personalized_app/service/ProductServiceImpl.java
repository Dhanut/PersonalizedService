package com.assignment.personalized_app.service;


import com.assignment.personalized_app.dto.OperationResponse;
import com.assignment.personalized_app.dto.ProductMetadataDTO;
import com.assignment.personalized_app.entity.ProductMetadata;
import com.assignment.personalized_app.repository.ProductMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class to handle product metadata operations.
 * Provides functionality to save product metadata and handle exceptions.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductMetadataRepository productMetadataRepository;

    /**
     * Saves product metadata.
     * This method creates a new product metadata entry in the database
     * using the information provided in the ProductMetadataDTO.
     * If the productId already exists, the request is rejected with a Bad Request (400).
     * The response is wrapped in an {@link OperationResponse} object to ensure
     * consistent API responses across the application.
     * @param dto ProductMetadataDTO containing productId, category, and brand
     * @return ResponseEntity containing OperationResponse with saved product data,
     * or an error message if the save fails
     */
    @Override
    public ResponseEntity<?> saveMetadata(ProductMetadataDTO dto) {
        try {
            logger.debug("Creating product metadata for productId: {}", dto.getProductId());

            // Check if productId already exists
            boolean exists = productMetadataRepository.existsById(dto.getProductId());
            if (exists) {
                String message = "Product metadata with productId " + dto.getProductId() + " already exists";
                logger.warn(message);
                return ResponseEntity.badRequest()
                        .body(new OperationResponse<>(false, message, null));
            }

            // Create new product
            ProductMetadata newProduct = ProductMetadata.builder()
                    .productId(dto.getProductId())
                    .category(dto.getCategory())
                    .brand(dto.getBrand())
                    .build();

            ProductMetadata savedProduct = productMetadataRepository.save(newProduct);
            logger.info("Product metadata created successfully for productId: {}", dto.getProductId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new OperationResponse<>(true, "Product metadata created successfully", savedProduct));

        } catch (Exception e) {
            logger.error("Error creating product metadata for productId: {}", dto.getProductId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResponse<>(false, "Failed to create product metadata: " + e.getMessage(), null));
        }
    }

    /**
     * Updates existing product metadata.
     * This method updates the category and brand of an existing product metadata entry
     * identified by the productId in the provided DTO. If the productId does not exist,
     * the request is rejected with a Bad Request (400).
     * The response is wrapped in an {@link OperationResponse} object to ensure
     * consistent API responses across the application.
     * @param dto ProductMetadataDTO containing productId, category, and brand
     * @return ResponseEntity containing OperationResponse with updated product data,
     * or an error message if the update fails
     */
    @Override
    public ResponseEntity<?> updateMetadata(ProductMetadataDTO dto) {
        try {
            logger.debug("Updating product metadata for productId: {}", dto.getProductId());

            // Check if product exists
            Optional<ProductMetadata> existingOpt =
                    productMetadataRepository.findById(dto.getProductId());

            if (existingOpt.isEmpty()) {
                String message = "Product metadata with productId "
                        + dto.getProductId() + " does not exist";
                logger.warn(message);

                return ResponseEntity.badRequest()
                        .body(new OperationResponse<>(false, message, null));
            }

            // Update existing product
            ProductMetadata existingProduct = existingOpt.get();
            existingProduct.setCategory(dto.getCategory());
            existingProduct.setBrand(dto.getBrand());

            ProductMetadata updatedProduct =
                    productMetadataRepository.save(existingProduct);

            logger.info("Product metadata updated successfully for productId: {}",
                    dto.getProductId());

            return ResponseEntity.ok(
                    new OperationResponse<>(
                            true,
                            "Product metadata updated successfully",
                            updatedProduct
                    )
            );

        } catch (Exception e) {
            logger.error("Error updating product metadata for productId: {}",
                    dto.getProductId(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResponse<>(
                            false,
                            "Failed to update product metadata: " + e.getMessage(),
                            null
                    ));
        }
    }


}
