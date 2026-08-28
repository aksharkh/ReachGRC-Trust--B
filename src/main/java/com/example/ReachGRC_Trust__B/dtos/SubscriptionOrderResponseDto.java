package com.example.ReachGRC_Trust__B.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionOrderResponseDto {
    private String orderId;
    private Long amount;
    private String currency;
    private String keyId;
    private Long companyId;
    private String plan;
    private boolean isSimulated;
}
