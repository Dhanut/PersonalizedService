package com.assignment.personalized_app.controller;

import com.assignment.personalized_app.dto.OperationResponse;
import com.assignment.personalized_app.dto.ProductMetadataDTO;
import com.assignment.personalized_app.dto.ShopperShelfDTO;
import com.assignment.personalized_app.service.ProductService;
import com.assignment.personalized_app.service.ShopperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * InternalController handles internal API endpoints for saving
 * shopper-related shelf data and product metadata.
 * This controller is intended for internal system use, allowing
 * the saving of shopper shelves and product metadata through
 * POST requests.
 **/
@RestController
@RequestMapping("/internal")
public class InternalController {

    private static final Logger logger = LoggerFactory.getLogger(InternalController.class);

    /**
     * Service layer for handling shopper-related operations.
     * Responsible for saving shopper shelves and any related business logic.
     */
    @Autowired
    private ShopperService shopperService;

    /**
     * Service layer for handling product-related operations.
     * Responsible for saving product metadata and related business logic.
     */
    @Autowired
    private ProductService productService;

    /**
     * Saves product metadata information.
     *
     * @param dto ProductMetadataDTO containing product metadata details.
     * @return ResponseEntity with the result of the save operation.
     * <p>
     * Example endpoint: POST /internal/product-metadata
     */
    @PostMapping("/product-metadata")
    public ResponseEntity<?> postProductMetadata(@RequestBody ProductMetadataDTO dto) {
        return productService.saveMetadata(dto);
    }

    /**
     * Updates existing product metadata information.
     *
     * @param dto ProductMetadataDTO containing updated product metadata details.
     * @return ResponseEntity with the result of the update operation.
     * <p>
     * Example endpoint: PUT /internal/product-metadata
     */
    @PutMapping("/product-metadata")
    public ResponseEntity<?> updateProductMetadata(@RequestBody ProductMetadataDTO dto) {
        return productService.updateMetadata(dto);
    }

    @PostMapping("/shopper-products")
    public ResponseEntity<?> postShopperProducts(@RequestBody ShopperShelfDTO dto) {
        try {
            OperationResponse<?> response = shopperService.saveShelf(dto);
            if (!response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

        } catch (Exception e) {
            logger.error("Error creating shopper shelf for shopperId {}: {}", dto.getShopperId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to create shopper shelf"));
        }
    }

    /**
     * Updates the shopper's shelf with new product data.
     *
     * @param dto ShopperShelfDTO containing shopper shelf details.
     * @return ResponseEntity with the result of the shelf update operation.
     * <p>
     * Example endpoint: PUT /internal/shopper-products
     */
    @PutMapping("/shopper-products")
    public ResponseEntity<?> updateShopperProducts(@RequestBody ShopperShelfDTO dto) {
        try {
            OperationResponse<?> response = shopperService.updateShelf(dto);
            if (!response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

        } catch (Exception e) {
            logger.error("Error replacing shopper shelf for shopperId {}: {}", dto.getShopperId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to replace shopper shelf"));
        }
    }

}

