package com.assignment.personalized_app.service;

import com.assignment.personalized_app.dto.OperationResponse;
import com.assignment.personalized_app.dto.ProductMetadataDTO;
import com.assignment.personalized_app.entity.ProductMetadata;
import com.assignment.personalized_app.repository.ProductMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductMetadataRepository productMetadataRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* =========================
       saveMetadata Tests
       ========================= */

    @Test
    void saveMetadata_Success() {
        ProductMetadataDTO dto = new ProductMetadataDTO();
        dto.setProductId("prod1");
        dto.setCategory("Electronics");
        dto.setBrand("Sony");

        when(productMetadataRepository.existsById("prod1")).thenReturn(false);

        ProductMetadata savedProduct = new ProductMetadata();
        savedProduct.setProductId("prod1");
        savedProduct.setCategory("Electronics");
        savedProduct.setBrand("Sony");

        when(productMetadataRepository.save(any(ProductMetadata.class))).thenReturn(savedProduct);

        ResponseEntity<?> responseEntity = productService.saveMetadata(dto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        OperationResponse<?> response = (OperationResponse<?>) responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Product metadata created successfully");
        assertThat(response.getData()).isEqualTo(savedProduct);

        verify(productMetadataRepository, times(1)).existsById("prod1");
        verify(productMetadataRepository, times(1)).save(any(ProductMetadata.class));
    }

    @Test
    void saveMetadata_AlreadyExists() {
        ProductMetadataDTO dto = new ProductMetadataDTO();
        dto.setProductId("prod1");

        when(productMetadataRepository.existsById("prod1")).thenReturn(true);

        ResponseEntity<?> responseEntity = productService.saveMetadata(dto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        OperationResponse<?> response = (OperationResponse<?>) responseEntity.getBody();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("already exists");

        verify(productMetadataRepository, times(1)).existsById("prod1");
        verify(productMetadataRepository, never()).save(any());
    }

    @Test
    void saveMetadata_Exception() {
        ProductMetadataDTO dto = new ProductMetadataDTO();
        dto.setProductId("prod1");

        when(productMetadataRepository.existsById(anyString())).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> responseEntity = productService.saveMetadata(dto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        OperationResponse<?> response = (OperationResponse<?>) responseEntity.getBody();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Failed to create product metadata");

        verify(productMetadataRepository, times(1)).existsById("prod1");
    }

    /* =========================
       updateMetadata Tests
       ========================= */

    @Test
    void updateMetadata_Success() {
        ProductMetadataDTO dto = new ProductMetadataDTO();
        dto.setProductId("prod1");
        dto.setCategory("Electronics");
        dto.setBrand("Sony");

        ProductMetadata existingProduct = new ProductMetadata();
        existingProduct.setProductId("prod1");
        existingProduct.setCategory("OldCategory");
        existingProduct.setBrand("OldBrand");

        when(productMetadataRepository.findById("prod1")).thenReturn(Optional.of(existingProduct));
        when(productMetadataRepository.save(existingProduct)).thenReturn(existingProduct);

        ResponseEntity<?> responseEntity = productService.updateMetadata(dto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        OperationResponse<?> response = (OperationResponse<?>) responseEntity.getBody();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Product metadata updated successfully");
        assertThat(response.getData()).isEqualTo(existingProduct);

        verify(productMetadataRepository, times(1)).findById("prod1");
        verify(productMetadataRepository, times(1)).save(existingProduct);
    }

    @Test
    void updateMetadata_NotFound() {
        ProductMetadataDTO dto = new ProductMetadataDTO();
        dto.setProductId("prod1");

        when(productMetadataRepository.findById("prod1")).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = productService.updateMetadata(dto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        OperationResponse<?> response = (OperationResponse<?>) responseEntity.getBody();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("does not exist");

        verify(productMetadataRepository, times(1)).findById("prod1");
        verify(productMetadataRepository, never()).save(any());
    }

    @Test
    void updateMetadata_Exception() {
        ProductMetadataDTO dto = new ProductMetadataDTO();
        dto.setProductId("prod1");

        when(productMetadataRepository.findById(anyString())).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> responseEntity = productService.updateMetadata(dto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        OperationResponse<?> response = (OperationResponse<?>) responseEntity.getBody();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Failed to update product metadata");

        verify(productMetadataRepository, times(1)).findById("prod1");
    }
}
