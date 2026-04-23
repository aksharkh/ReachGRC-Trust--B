package com.example.ReachGRC_Trust__B.repository;

import com.example.ReachGRC_Trust__B.entity.Logo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogoRepository extends JpaRepository<Logo, Long> {
    Logo findByLogoId(Long logoId);

    boolean existsByLogoId(Long logoId);

    boolean existsByFileName(String fileName);

    List<Logo> findByCompanyId(Long companyId);

    void deleteByLogoId(Long logoId);

    void deleteByCompanyId(Long companyId);

    Logo findByLogoIdAndCompanyId(Long logoId, Long companyId);
}
