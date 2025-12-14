package com.assignment.personalized_app.controller;

import com.assignment.personalized_app.service.ShopperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;

@RestController
@RequestMapping("/external")
public class ExternalController {
    /**
     * Service layer for handling shopper-related operations.
     * Responsible for saving shopper shelves and any related business logic.
     */
    @Autowired
    private ShopperService shopperService;


    /**
     * Retrieves products for a shopper with optional category/brand filters and pagination.
     *
     * @param shopperId Shopper identifier (required)
     * @param category  Product category filter (optional)
     * @param brand     Product brand filter (optional)
     * @param limit     Maximum products per page (default=10, max=100)
     * @param page      Page number for pagination (default=0)
     * @return ResponseEntity containing paginated products
     */
    @GetMapping("/{shopperId}/products")
    public ResponseEntity<?> getProductsByShopper(
            @PathVariable String shopperId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    ) {
        // Handle defaults and clamp limits
        if (Objects.isNull(limit) || limit <= 0) limit = 10;
        if (limit > 100) limit = 100;

        if (Objects.isNull(page) || page < 0) page = 0;

        return shopperService.getProductsByShopper(shopperId, category, brand, limit, page);
    }
}
