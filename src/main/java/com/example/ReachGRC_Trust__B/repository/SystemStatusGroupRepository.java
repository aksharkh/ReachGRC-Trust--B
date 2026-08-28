package com.example.ReachGRC_Trust__B.repository;

import com.example.ReachGRC_Trust__B.entity.SystemStatusGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemStatusGroupRepository extends JpaRepository<SystemStatusGroup, Long> {
    List<SystemStatusGroup> findAllByOrderByOrderIndexAsc();
}
