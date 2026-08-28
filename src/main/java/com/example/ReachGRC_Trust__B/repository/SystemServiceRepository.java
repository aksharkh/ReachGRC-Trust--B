package com.example.ReachGRC_Trust__B.repository;

import com.example.ReachGRC_Trust__B.entity.SystemService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemServiceRepository extends JpaRepository<SystemService, Long> {
}
