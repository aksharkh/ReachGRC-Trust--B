package com.example.ReachGRC_Trust__B.repository;

import com.example.ReachGRC_Trust__B.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Resource findByFileId(Long fileId);
    Boolean existsByFileId(Long fileId);
    Boolean existsByFileName(String fileName);
    List<Resource> findByCompanyId(Long companyId);
    void deleteByFileId(Long fileId);
    void deleteByCompanyId(Long companyId);
}