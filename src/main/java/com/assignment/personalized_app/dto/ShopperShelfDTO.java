package com.assignment.personalized_app.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ShopperShelfDTO {
    private String shopperId;
    private List<ItemDTO> shelf;

}
