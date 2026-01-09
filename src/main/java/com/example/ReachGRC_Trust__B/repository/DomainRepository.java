package com.example.ReachGRC_Trust__B.repository;

import com.example.ReachGRC_Trust__B.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Long> {

    List<Domain> findByCompanyId(Long companyId);

    boolean existsByCompanyIdAndName(Long companyId, String name);
}
