package com.example.ReachGRC_Trust__B.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionVerifyRequestDto {
    private Long companyId;
    private String plan;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
