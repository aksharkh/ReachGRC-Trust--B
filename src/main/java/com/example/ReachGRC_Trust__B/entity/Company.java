package com.example.ReachGRC_Trust__B.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "companies")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "company_name", nullable = false, unique = true)
    private String companyName;

    @Column(name = "statement", columnDefinition = "TEXT")
    private String statement;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Domain> domains = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "api_key", unique = true)
    private String apiKey;

    @Column(name = "api_key_status")
    private String apiKeyStatus;

    @Column(name = "api_key_issued_at")
    private LocalDateTime apiKeyIssuedAt;

    @Column(name = "api_key_expires_at")
    private LocalDateTime apiKeyExpiresAt;

    @Column(name = "subscription_plan")
    private String subscriptionPlan;

    @Column(name = "subscription_status")
    private String subscriptionStatus;

    @Column(name = "subscription_expires_at")
    private LocalDateTime subscriptionExpiresAt;

    @OneToMany(mappedBy = "company")
    private List<Resource> resources;

    //helper
    public void addDomain(Domain domain){
        domains.add(domain);
        domain.setCompany(this);
    }

    public void removeDomain(Domain domain){
        domains.remove(domain);
        domain.setCompany(null);
    }


    @PrePersist
    protected void  onCreate(){
        if(isActive == null) {
            isActive = true;
        }
    }


}
