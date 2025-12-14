package com.assignment.personalized_app.service;

import com.assignment.personalized_app.dto.*;
import com.assignment.personalized_app.entity.ProductMetadata;
import com.assignment.personalized_app.entity.ShopperProduct;
import com.assignment.personalized_app.repository.ProductMetadataRepository;
import com.assignment.personalized_app.repository.ShopperProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing shopper shelves and product retrieval.
 */
@Service
public class ShopperServiceImpl implements ShopperService {

    private static final Logger logger = LoggerFactory.getLogger(ShopperServiceImpl.class);
    private static final int BATCH_SIZE = 50;

    private final ShopperProductRepository shopperProductRepository;
    private final ProductMetadataRepository productMetadataRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ShopperServiceImpl(ShopperProductRepository shopperProductRepository,
                              ProductMetadataRepository productMetadataRepository) {
        this.shopperProductRepository = shopperProductRepository;
        this.productMetadataRepository = productMetadataRepository;
    }

    /* ============================================================
       SAVE SHELF
       ============================================================ */

    /**
     * Saves a new shelf for a shopper.
     */
    @Override
    @Transactional
    public OperationResponse<?> saveShelf(ShopperShelfDTO dto) {

        if (isInvalidShopper(dto)) {
            return failure("Invalid shopperId");
        }

        String shopperId = dto.getShopperId();
        logger.info("Creating shelf for shopperId={}", shopperId);

        if (shelfAlreadyExists(shopperId)) {
            return failure("Shelf already exists for shopperId=" + shopperId + ". Use updateShelf.");
        }

        Map<String, BigDecimal> incomingMap = buildIncomingMap(dto.getShelf());
        if (incomingMap.isEmpty()) {
            return failure("No valid products found in shelf payload");
        }

        ValidationResult validationResult = validateProductIds(incomingMap.keySet());
        if (!validationResult.isValid()) {
            return failure("ProductIds missing in product metadata: " + validationResult.missingIds());
        }

        List<ShopperProduct> entities = buildShopperProducts(shopperId, incomingMap);
        shopperProductRepository.saveAll(entities);

        logger.info("Shelf created for shopperId={}, totalItems={}", shopperId, entities.size());

        return success(
                "Shopper shelf created successfully",
                Map.of("shopperId", shopperId, "totalItems", entities.size())
        );
    }

    /* ============================================================
       UPDATE SHELF
       ============================================================ */

    /**
     * Updates an existing shelf by inserting new products and updating changed relevancy scores.
     */
    @Override
    @Transactional
    public OperationResponse<?> updateShelf(ShopperShelfDTO dto) {

        if (isInvalidShopper(dto)) {
            return failure("Invalid shopperId");
        }

        String shopperId = dto.getShopperId();
        logger.info("Updating shelf for shopperId={}", shopperId);

        Map<String, BigDecimal> incomingMap = buildIncomingMap(dto.getShelf());
        if (incomingMap.isEmpty()) {
            return failure("Shelf payload is empty");
        }

        ValidationResult validationResult = validateProductIds(incomingMap.keySet());
        if (validationResult.allInvalid()) {
            return failureWithData(
                    "No valid products found for shopperId=" + shopperId,
                    Map.of(
                            "insertedProductIds", List.of(),
                            "updatedProductIds", List.of(),
                            "notSavedProductIds", validationResult.missingIds()
                    )
            );
        }

        Map<String, BigDecimal> existingMap =
                fetchExistingRelevancyMap(shopperId, validationResult.validIds());

        List<String> inserted = new ArrayList<>();
        List<String> updated = new ArrayList<>();

        processUpdatesAndInserts(
                shopperId,
                incomingMap,
                validationResult.validIds(),
                existingMap,
                inserted,
                updated
        );

        return success(
                buildUpdateMessage(shopperId, inserted, updated, validationResult.missingIds()),
                Map.of(
                        "insertedProductIds", inserted,
                        "updatedProductIds", updated,
                        "notSavedProductIds", validationResult.missingIds()
                )
        );
    }

    /* ============================================================
       FETCH PRODUCTS
       ============================================================ */

    /**
     * Retrieves paginated products for a shopper with optional filters.
     */
    @Override
    public ResponseEntity<?> getProductsByShopper(
            String shopperId,
            String category,
            String brand,
            Integer pageSize,
            Integer pageNumber
    ) {
        try {
            PageRequest pageable = PageRequest.of(pageNumber, pageSize);

            Page<ShopperProduct> page =
                    shopperProductRepository.findByShopperWithFilters(
                            shopperId,
                            normalize(category),
                            normalize(brand),
                            pageable
                    );

            PageResponseDTO<ShopperProductDTO> response =
                    mapToPageResponse(page, pageNumber);

            return ResponseEntity.ok(
                    success("Products fetched successfully", response)
            );

        } catch (Exception e) {
            logger.error("Error fetching products for shopperId={}", shopperId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(failure("Failed to fetch products for shopper"));
        }
    }

    /* ============================================================
       HELPER METHODS
       ============================================================ */

    private boolean isInvalidShopper(ShopperShelfDTO dto) {
        return dto == null || dto.getShopperId() == null || dto.getShopperId().isBlank();
    }

    private boolean shelfAlreadyExists(String shopperId) {
        return !shopperProductRepository.findByShopperId(shopperId).isEmpty();
    }

    private Map<String, BigDecimal> buildIncomingMap(List<ItemDTO> shelf) {
        if (shelf == null || shelf.isEmpty()) return Map.of();

        return shelf.stream()
                .filter(i -> i.getProductId() != null && i.getRelevancyScore() != null)
                .collect(Collectors.toMap(
                        ItemDTO::getProductId,
                        ItemDTO::getRelevancyScore,
                        (a, b) -> b
                ));
    }

    private ValidationResult validateProductIds(Set<String> productIds) {
        Set<String> valid = new HashSet<>(
                productMetadataRepository.findExistingProductIds(new ArrayList<>(productIds))
                        .orElse(List.of())
        );

        List<String> missing = productIds.stream()
                .filter(id -> !valid.contains(id))
                .toList();

        return new ValidationResult(valid, missing);
    }

    private List<ShopperProduct> buildShopperProducts(
            String shopperId,
            Map<String, BigDecimal> incomingMap
    ) {
        return incomingMap.keySet().stream()
                .map(pid -> ShopperProduct.builder()
                        .shopperId(shopperId)
                        .product(entityManager.getReference(ProductMetadata.class, pid))
                        .relevancyScore(incomingMap.get(pid))
                        .build())
                .toList();
    }

    private Map<String, BigDecimal> fetchExistingRelevancyMap(
            String shopperId,
            Set<String> validProductIds
    ) {
        Map<String, BigDecimal> map = new HashMap<>();

        shopperProductRepository.findExistingForShopper(shopperId, new ArrayList<>(validProductIds))
                .orElse(List.of())
                .forEach(row -> map.put((String) row[0], (BigDecimal) row[1]));

        return map;
    }

    private void processUpdatesAndInserts(
            String shopperId,
            Map<String, BigDecimal> incomingMap,
            Set<String> validProductIds,
            Map<String, BigDecimal> existingMap,
            List<String> inserted,
            List<String> updated
    ) {
        int batch = 0;

        for (String productId : validProductIds) {
            BigDecimal incomingScore = incomingMap.get(productId);

            if (existingMap.containsKey(productId)) {
                if (incomingScore.compareTo(existingMap.get(productId)) != 0) {
                    shopperProductRepository.updateRelevancyScore(
                            shopperId, productId, incomingScore
                    );
                    updated.add(productId);
                }
            } else {
                entityManager.persist(
                        ShopperProduct.builder()
                                .shopperId(shopperId)
                                .product(entityManager.getReference(ProductMetadata.class, productId))
                                .relevancyScore(incomingScore)
                                .build()
                );
                inserted.add(productId);

                if (++batch % BATCH_SIZE == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
        }

        entityManager.flush();
        entityManager.clear();
    }

    private PageResponseDTO<ShopperProductDTO> mapToPageResponse(
            Page<ShopperProduct> page,
            int pageNumber
    ) {
        List<ShopperProductDTO> content = page.getContent().stream()
                .map(sp -> ShopperProductDTO.builder()
                        .productId(sp.getProduct().getProductId())
                        .relevancyScore(sp.getRelevancyScore())
                        .category(sp.getProduct().getCategory())
                        .brand(sp.getProduct().getBrand())
                        .build())
                .toList();

        return PageResponseDTO.<ShopperProductDTO>builder()
                .content(content)
                .currentPage(pageNumber)
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    private String buildUpdateMessage(
            String shopperId,
            List<String> inserted,
            List<String> updated,
            List<String> skipped
    ) {
        return String.format(
                "Processed shelf for shopperId=%s (inserted=%d, updated=%d, skipped=%d)",
                shopperId, inserted.size(), updated.size(), skipped.size()
        );
    }

    private <T> OperationResponse<T> success(String message, T data) {
        return new OperationResponse<>(true, message, data);
    }

    private OperationResponse<?> failure(String message) {
        return new OperationResponse<>(false, message, Map.of());
    }

    private OperationResponse<?> failureWithData(String message, Object data) {
        return new OperationResponse<>(false, message, data);
    }

    /* ============================================================
       INTERNAL RECORD
       ============================================================ */

    private record ValidationResult(Set<String> validIds, List<String> missingIds) {
        boolean isValid() { return missingIds.isEmpty(); }
        boolean allInvalid() { return validIds.isEmpty(); }
    }
}
