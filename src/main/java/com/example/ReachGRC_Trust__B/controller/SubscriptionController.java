package com.example.ReachGRC_Trust__B.controller;

import com.example.ReachGRC_Trust__B.dtos.SubscriptionOrderRequestDto;
import com.example.ReachGRC_Trust__B.dtos.SubscriptionOrderResponseDto;
import com.example.ReachGRC_Trust__B.dtos.SubscriptionVerifyRequestDto;
import com.example.ReachGRC_Trust__B.dtos.SubscriptionVerifyResponseDto;
import com.example.ReachGRC_Trust__B.service.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody SubscriptionOrderRequestDto request) {
        log.info("Received controller request to create order for company ID: {} and plan: {}", 
                request.getCompanyId(), request.getPlan());
        try {
            SubscriptionOrderResponseDto response = subscriptionService.createOrder(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid order arguments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Internal error creating subscription order", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to create payment order: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody SubscriptionVerifyRequestDto request) {
        log.info("Received controller request to verify payment for company ID: {} and order: {}", 
                request.getCompanyId(), request.getRazorpayOrderId());
        try {
            SubscriptionVerifyResponseDto response = subscriptionService.verifyPayment(request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Invalid verification arguments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("Illegal action: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            log.error("Internal error verifying payment", e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "error", "Internal verification failure: " + e.getMessage()));
        }
    }

    @PostMapping("/downgrade")
    public ResponseEntity<?> downgradeSubscription(@RequestBody com.example.ReachGRC_Trust__B.dtos.SubscriptionDowngradeRequestDto request) {
        log.info("Received controller request to downgrade/cancel subscription for company ID: {}", 
                request.getCompanyId());
        try {
            SubscriptionVerifyResponseDto response = subscriptionService.downgradeSubscription(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid downgrade arguments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            log.error("Internal error downgrading subscription", e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "error", "Downgrade execution failure: " + e.getMessage()));
        }
    }
}
