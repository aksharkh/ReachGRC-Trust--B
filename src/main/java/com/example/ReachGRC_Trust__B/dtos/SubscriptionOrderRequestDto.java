package com.example.ReachGRC_Trust__B.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionOrderRequestDto {
    private Long companyId;
    private String plan; // e.g. "GROWTH", "ENTERPRISE"
}
