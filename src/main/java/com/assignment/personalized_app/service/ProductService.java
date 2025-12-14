package com.assignment.personalized_app.service;

import com.assignment.personalized_app.dto.ProductMetadataDTO;
import org.springframework.http.ResponseEntity;

public interface ProductService {
    ResponseEntity<?> saveMetadata(ProductMetadataDTO dto);
    ResponseEntity<?> updateMetadata(ProductMetadataDTO dto);

}
