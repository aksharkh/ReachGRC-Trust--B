package com.example.ReachGRC_Trust__B.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionVerifyResponseDto {
    private boolean success;
    private String message;
    private String plan;
    private String status;
}
