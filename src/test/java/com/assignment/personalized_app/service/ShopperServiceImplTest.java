package com.assignment.personalized_app.service;

import com.assignment.personalized_app.dto.ItemDTO;
import com.assignment.personalized_app.dto.OperationResponse;
import com.assignment.personalized_app.dto.ShopperShelfDTO;
import com.assignment.personalized_app.entity.ProductMetadata;
import com.assignment.personalized_app.entity.ShopperProduct;
import com.assignment.personalized_app.repository.ProductMetadataRepository;
import com.assignment.personalized_app.repository.ShopperProductRepository;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ShopperServiceImplTest {

    @Mock
    private ShopperProductRepository shopperProductRepository;

    @Mock
    private ProductMetadataRepository productMetadataRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ShopperServiceImpl shopperService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // No manual injection needed; @InjectMocks handles constructor injection
    }

    /* ===========================================
       saveShelf() Tests
       =========================================== */

    @Test
    void testSaveShelf_InvalidShopperId() {
        ShopperShelfDTO dto = new ShopperShelfDTO(null, List.of());

        OperationResponse<?> response = shopperService.saveShelf(dto);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Invalid shopperId");
    }

    @Test
    void testSaveShelf_ShelfAlreadyExists() {
        ShopperShelfDTO dto = new ShopperShelfDTO("shopper1", List.of());
        when(shopperProductRepository.findByShopperId("shopper1"))
                .thenReturn(List.of(new ShopperProduct()));

        OperationResponse<?> response = shopperService.saveShelf(dto);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Shelf already exists");
    }

    @Test
    void testSaveShelf_Success() {
        ShopperShelfDTO dto = new ShopperShelfDTO();
        dto.setShopperId("shopper1");
        dto.setShelf(List.of(new ItemDTO("prod1", BigDecimal.TEN)));

        ProductMetadata product = new ProductMetadata();
        product.setProductId("prod1");

        // Mock repository to return the product
        when(productMetadataRepository.findAllById(anyList()))
                .thenReturn(List.of(product));

        // Mock findByShopperId to return empty
        when(shopperProductRepository.findByShopperId("shopper1"))
                .thenReturn(List.of());

        // Mock saveAll
        when(shopperProductRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OperationResponse<?> response = shopperService.saveShelf(dto);

        assertTrue(response.isSuccess());
    }

    /* ===========================================
       updateShelf() Tests
       =========================================== */

    @Test
    void testUpdateShelf_InvalidShopperId() {
        ShopperShelfDTO dto = new ShopperShelfDTO(null, List.of());

        OperationResponse<?> response = shopperService.updateShelf(dto);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Invalid shopperId");
    }

    @Test
    void testUpdateShelf_EmptyShelf() {
        ShopperShelfDTO dto = new ShopperShelfDTO("shopper1", List.of());

        OperationResponse<?> response = shopperService.updateShelf(dto);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Shelf payload is empty");
    }

    /* ===========================================
       getProductsByShopper() Tests
       =========================================== */

    @Test
    void testGetProductsByShopper_Success() {
        ProductMetadata productM = new ProductMetadata();
        productM.setProductId("prod1");
        productM.setCategory("Cat");
        productM.setBrand("Brand");

        ShopperProduct product = ShopperProduct.builder()
                .shopperId("shopper1")
                .product(productM)
                .relevancyScore(BigDecimal.TEN)
                .build();

        Page<ShopperProduct> page = new PageImpl<>(List.of(product));
        when(shopperProductRepository.findByShopperWithFilters(anyString(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        ResponseEntity<?> responseEntity =
                shopperService.getProductsByShopper("shopper1", null, null, 10, 0);

        OperationResponse<?> response = (OperationResponse<?>) responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).contains("Products fetched successfully");
    }

    @Test
    void testGetProductsByShopper_Exception() {
        when(shopperProductRepository.findByShopperWithFilters(anyString(), any(), any(), any(Pageable.class)))
                .thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> responseEntity =
                shopperService.getProductsByShopper("shopper1", null, null, 10, 0);

        OperationResponse<?> response = (OperationResponse<?>) responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Failed to fetch products");
    }
}
