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
