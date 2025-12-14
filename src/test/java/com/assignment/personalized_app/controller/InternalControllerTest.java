package com.assignment.personalized_app.controller;

import com.assignment.personalized_app.dto.OperationResponse;
import com.assignment.personalized_app.dto.ProductMetadataDTO;
import com.assignment.personalized_app.dto.ShopperShelfDTO;
import com.assignment.personalized_app.dto.ItemDTO;
import com.assignment.personalized_app.service.ProductService;
import com.assignment.personalized_app.service.ShopperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InternalControllerTest {

    @InjectMocks
    private InternalController internalController;

    @Mock
    private ShopperService shopperService;

    @Mock
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* =========================
       Product Metadata Tests
       ========================= */

    @Test
    void postProductMetadata_Success() {
        ProductMetadataDTO dto = new ProductMetadataDTO();
        OperationResponse<String> response = new OperationResponse<>(true, "Product saved", null);
        ResponseEntity<OperationResponse<String>> responseEntity = ResponseEntity.ok(response);

        when(productService.saveMetadata(any(ProductMetadataDTO.class)))
                .thenReturn((ResponseEntity)responseEntity);

        ResponseEntity<?> result = internalController.postProductMetadata(dto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        verify(productService, times(1)).saveMetadata(dto);
    }

    @Test
    void updateProductMetadata_Success() {
        ProductMetadataDTO dto = new ProductMetadataDTO();
        OperationResponse<String> response = new OperationResponse<>(true, "Product updated", null);
        ResponseEntity<OperationResponse<String>> entity = ResponseEntity.ok(response);

        when(productService.updateMetadata(any(ProductMetadataDTO.class))).thenReturn((ResponseEntity)entity);

        ResponseEntity<?> result = internalController.updateProductMetadata(dto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        verify(productService, times(1)).updateMetadata(dto);
    }

    /* =========================
       Shopper Products Tests
       ========================= */

    @Test
    void postShopperProducts_Success() {
        ShopperShelfDTO dto = new ShopperShelfDTO("shopper1",
                java.util.List.of(new ItemDTO("prod1", BigDecimal.TEN)));
        OperationResponse<?> response = new OperationResponse<>(true, "Shelf saved", null);

        when(shopperService.saveShelf(any(ShopperShelfDTO.class))).thenReturn((OperationResponse)response);

        ResponseEntity<?> result = internalController.postShopperProducts(dto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        verify(shopperService, times(1)).saveShelf(dto);
    }

    @Test
    void postShopperProducts_Failure() {
        ShopperShelfDTO dto = new ShopperShelfDTO("shopper1", java.util.List.of());
        OperationResponse<?> response = new OperationResponse<>(false, "Invalid shelf", null);

        when(shopperService.saveShelf(any(ShopperShelfDTO.class))).thenReturn((OperationResponse)response);

        ResponseEntity<?> result = internalController.postShopperProducts(dto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo(response);
        verify(shopperService, times(1)).saveShelf(dto);
    }

    @Test
    void postShopperProducts_Exception() {
        ShopperShelfDTO dto = new ShopperShelfDTO("shopper1", java.util.List.of());

        when(shopperService.saveShelf(any(ShopperShelfDTO.class))).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> result = internalController.postShopperProducts(dto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isInstanceOf(Map.class);
        assertThat(((Map<?, ?>) result.getBody()).get("message")).isEqualTo("Failed to create shopper shelf");
    }

    @Test
    void updateShopperProducts_Success() {
        ShopperShelfDTO dto = new ShopperShelfDTO("shopper1",
                java.util.List.of(new ItemDTO("prod1", BigDecimal.TEN)));
        OperationResponse<?> response = new OperationResponse<>(true, "Shelf updated", null);

        when(shopperService.updateShelf(any(ShopperShelfDTO.class))).thenReturn((OperationResponse)response);

        ResponseEntity<?> result = internalController.updateShopperProducts(dto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        verify(shopperService, times(1)).updateShelf(dto);
    }

    @Test
    void updateShopperProducts_Failure() {
        ShopperShelfDTO dto = new ShopperShelfDTO("shopper1", java.util.List.of());
        OperationResponse<?> response = new OperationResponse<>(false, "Shelf empty", null);

        when(shopperService.updateShelf(any(ShopperShelfDTO.class))).thenReturn((OperationResponse)response);

        ResponseEntity<?> result = internalController.updateShopperProducts(dto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo(response);
        verify(shopperService, times(1)).updateShelf(dto);
    }

    @Test
    void updateShopperProducts_Exception() {
        ShopperShelfDTO dto = new ShopperShelfDTO("shopper1", java.util.List.of());

        when(shopperService.updateShelf(any(ShopperShelfDTO.class))).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> result = internalController.updateShopperProducts(dto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isInstanceOf(Map.class);
        assertThat(((Map<?, ?>) result.getBody()).get("message")).isEqualTo("Failed to replace shopper shelf");
    }
}
