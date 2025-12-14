package com.assignment.personalized_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
