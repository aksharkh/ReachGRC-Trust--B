package com.example.ReachGRC_Trust__B.repository;

import com.example.ReachGRC_Trust__B.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByCompanyName(String companyName);

    List<Company> findByIsActive(Boolean isActive);

    boolean existsByCompanyName(String companyName);


//    @Query("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.domains WHERE cisActive = true")
//    List<Company> findAllActiveWithDomains();



}
