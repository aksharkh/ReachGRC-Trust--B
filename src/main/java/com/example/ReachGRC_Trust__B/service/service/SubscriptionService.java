package com.example.ReachGRC_Trust__B.service.service;

import com.example.ReachGRC_Trust__B.dtos.SubscriptionOrderRequestDto;
import com.example.ReachGRC_Trust__B.dtos.SubscriptionOrderResponseDto;
import com.example.ReachGRC_Trust__B.dtos.SubscriptionVerifyRequestDto;
import com.example.ReachGRC_Trust__B.dtos.SubscriptionVerifyResponseDto;

public interface SubscriptionService {
    SubscriptionOrderResponseDto createOrder(SubscriptionOrderRequestDto request);
    SubscriptionVerifyResponseDto verifyPayment(SubscriptionVerifyRequestDto request);
    SubscriptionVerifyResponseDto downgradeSubscription(com.example.ReachGRC_Trust__B.dtos.SubscriptionDowngradeRequestDto request);

}
