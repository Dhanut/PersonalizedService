package com.assignment.personalized_app.controller;

import com.assignment.personalized_app.service.ShopperService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExternalController.class)
class ExternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShopperService shopperService;

    @Test
    void testGetProductsByShopper_Defaults() throws Exception {
        // Mock service response with cast to ResponseEntity
        Mockito.when(shopperService.getProductsByShopper(
                        eq("shopper1"), isNull(), isNull(), eq(10), eq(0)))
                .thenReturn((ResponseEntity) ResponseEntity.ok("mocked"));

        mockMvc.perform(get("/external/shopper1/products"))
                .andExpect(status().isOk())
                .andExpect(content().string("mocked"));
    }

    @Test
    void testGetProductsByShopper_WithParams() throws Exception {
        // Mock service response with cast to ResponseEntity
        Mockito.when(shopperService.getProductsByShopper(
                        eq("shopper1"), eq("Electronics"), eq("Sony"), eq(50), eq(2)))
                .thenReturn((ResponseEntity) ResponseEntity.ok("mocked"));

        mockMvc.perform(get("/external/shopper1/products")
                        .param("category", "Electronics")
                        .param("brand", "Sony")
                        .param("limit", "50")
                        .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("mocked"));
    }
}
