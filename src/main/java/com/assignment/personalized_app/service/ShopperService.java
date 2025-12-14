package com.assignment.personalized_app.service;

import com.assignment.personalized_app.dto.OperationResponse;
import com.assignment.personalized_app.dto.ShopperShelfDTO;
import org.springframework.http.ResponseEntity;

public interface ShopperService {
    OperationResponse<?>  saveShelf(ShopperShelfDTO dto);
    OperationResponse<?> updateShelf(ShopperShelfDTO dto);
    ResponseEntity<?> getProductsByShopper(String shopperId,String category,String brand,Integer limit,Integer page);
}
