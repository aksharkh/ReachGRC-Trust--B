package com.example.ReachGRC_Trust__B.service.serviceImpl;

import com.example.ReachGRC_Trust__B.config.RazorpayConfig;
import com.example.ReachGRC_Trust__B.dtos.SubscriptionOrderRequestDto;
import com.example.ReachGRC_Trust__B.dtos.SubscriptionOrderResponseDto;
import com.example.ReachGRC_Trust__B.dtos.SubscriptionVerifyRequestDto;
import com.example.ReachGRC_Trust__B.dtos.SubscriptionVerifyResponseDto;
import com.example.ReachGRC_Trust__B.entity.Company;
import com.example.ReachGRC_Trust__B.repository.CompanyRepository;
import com.example.ReachGRC_Trust__B.service.service.SubscriptionService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final CompanyRepository companyRepository;
    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;

    @Value("${razorpay.simulator.enabled:true}")
    private boolean simulatorEnabled;

    @Value("${razorpay.admin.secure-pin:998877}")
    private String securePin;


    @Override
    @Transactional
    public SubscriptionOrderResponseDto createOrder(SubscriptionOrderRequestDto request) {
        log.info("Creating Razorpay order for company ID: {} and plan: {}", 
                request.getCompanyId(), request.getPlan());

        Optional<Company> companyOpt = companyRepository.findById(request.getCompanyId());
        if (companyOpt.isEmpty()) {
            throw new IllegalArgumentException("Company not found with ID: " + request.getCompanyId());
        }

        // Determine price in paise (1 INR = 100 paise)
        long priceInPaise = 0;
        if ("GROWTH".equalsIgnoreCase(request.getPlan())) {
            priceInPaise = 499900; // 4,999 INR
        } else if ("ENTERPRISE".equalsIgnoreCase(request.getPlan())) {
            priceInPaise = 1999900; // 19,999 INR
        } else {
            throw new IllegalArgumentException("Invalid subscription plan name: " + request.getPlan());
        }

        // Use mock simulator if enabled and credentials are dummy, or as a general fallback
        if (simulatorEnabled && isMockCredentials()) {
            return generateMockOrder(request.getCompanyId(), request.getPlan(), priceInPaise);
        }

        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", priceInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_company_" + request.getCompanyId());

            Order order = razorpayClient.orders.create(orderRequest);

            return SubscriptionOrderResponseDto.builder()
                    .orderId(order.get("id"))
                    .amount(Long.valueOf(order.get("amount").toString()))
                    .currency(order.get("currency"))
                    .keyId(razorpayConfig.getKeyId())
                    .companyId(request.getCompanyId())
                    .plan(request.getPlan().toUpperCase())
                    .isSimulated(false)
                    .build();

        } catch (Exception e) {
            log.warn("Razorpay API order creation failed: {}", e.getMessage());
            if (simulatorEnabled) {
                log.info("Simulator fallback enabled. Generating mock order.");
                return generateMockOrder(request.getCompanyId(), request.getPlan(), priceInPaise);
            }
            throw new RuntimeException("Failed to generate order from Razorpay gateway: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public SubscriptionVerifyResponseDto verifyPayment(SubscriptionVerifyRequestDto request) {
        log.info("Verifying subscription payment for company ID: {} with order ID: {}", 
                request.getCompanyId(), request.getRazorpayOrderId());

        Optional<Company> companyOpt = companyRepository.findById(request.getCompanyId());
        if (companyOpt.isEmpty()) {
            throw new IllegalArgumentException("Company not found with ID: " + request.getCompanyId());
        }

        Company company = companyOpt.get();
        boolean verified = false;

        // Check if it's a simulated order
        if (request.getRazorpayOrderId() != null && request.getRazorpayOrderId().startsWith("order_mock_")) {
            if (!simulatorEnabled) {
                throw new IllegalStateException("Payment simulation is disabled in this environment.");
            }
            verified = true;
            log.info("Simulated transaction verified successfully.");
        } else {
            try {
                // Perform actual signature verification using Razorpay SDK
                JSONObject attributes = new JSONObject();
                attributes.put("razorpay_order_id", request.getRazorpayOrderId());
                attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
                attributes.put("razorpay_signature", request.getRazorpaySignature());

                com.razorpay.Utils.verifyPaymentSignature(attributes, razorpayConfig.getKeySecret());
                verified = true;
                log.info("Razorpay transaction verified successfully.");
            } catch (Exception e) {
                log.error("Signature verification failed", e);
                // Fallback for local testing if mock keys are detected and simulator is enabled
                if (simulatorEnabled && isMockCredentials()) {
                    verified = true;
                    log.warn("Simulator enabled with mock keys: forcing signature verification success for testing.");
                } else {
                    throw new IllegalArgumentException("Invalid payment signature or credentials.", e);
                }
            }
        }

        if (verified) {
            // Update company subscription details
            company.setSubscriptionPlan(request.getPlan().toUpperCase());
            company.setSubscriptionStatus("ACTIVE");
            company.setSubscriptionExpiresAt(LocalDateTime.now().plusMonths(1));
            
            // If the plan is upgraded, verify and generate API key if not exists
            if (company.getApiKey() == null || company.getApiKey().isEmpty()) {
                String newKey = "grc_" + UUID.randomUUID().toString().replace("-", "");
                company.setApiKey(newKey);
                company.setApiKeyStatus("ACTIVE");
                company.setApiKeyIssuedAt(LocalDateTime.now());
                company.setApiKeyExpiresAt(LocalDateTime.now().plusYears(1));
            }
            
            companyRepository.save(company);

            return SubscriptionVerifyResponseDto.builder()
                    .success(true)
                    .message("Subscription activated successfully")
                    .plan(request.getPlan().toUpperCase())
                    .status("ACTIVE")
                    .build();
        } else {
            return SubscriptionVerifyResponseDto.builder()
                    .success(false)
                    .message("Payment verification failed")
                    .build();
        }
    }

    private boolean isMockCredentials() {
        String keyId = razorpayConfig.getKeyId();
        String keySecret = razorpayConfig.getKeySecret();
        return keyId == null || 
               keyId.startsWith("rzp_test_mock") || 
               "mockSecret123".equals(keySecret);
    }

    private SubscriptionOrderResponseDto generateMockOrder(Long companyId, String plan, long amount) {
        String mockOrderId = "order_mock_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
        log.info("Generated mock order: {}", mockOrderId);

        return SubscriptionOrderResponseDto.builder()
                .orderId(mockOrderId)
                .amount(amount)
                .currency("INR")
                .keyId(razorpayConfig.getKeyId())
                .companyId(companyId)
                .plan(plan.toUpperCase())
                .isSimulated(true)
                .build();
    }

    @Override
    @Transactional
    public SubscriptionVerifyResponseDto downgradeSubscription(com.example.ReachGRC_Trust__B.dtos.SubscriptionDowngradeRequestDto request) {
        log.info("Request to downgrade/cancel subscription for company ID: {}", request.getCompanyId());

        if (request.getAdminPassword() == null || !request.getAdminPassword().equals(securePin)) {
            throw new IllegalArgumentException("Invalid administrative secure PIN.");
        }

        Optional<Company> companyOpt = companyRepository.findById(request.getCompanyId());
        if (companyOpt.isEmpty()) {
            throw new IllegalArgumentException("Company not found with ID: " + request.getCompanyId());
        }

        Company company = companyOpt.get();
        company.setSubscriptionPlan("FREE");
        company.setSubscriptionStatus("CANCELLED");
        company.setSubscriptionExpiresAt(null);
        company.setApiKeyStatus("INACTIVE"); // Revoke API access

        companyRepository.save(company);

        return SubscriptionVerifyResponseDto.builder()
                .success(true)
                .message("Subscription cancelled and downgraded successfully.")
                .plan("FREE")
                .status("CANCELLED")
                .build();
    }
}
