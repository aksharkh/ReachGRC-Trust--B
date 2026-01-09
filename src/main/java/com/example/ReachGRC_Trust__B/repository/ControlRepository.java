package com.example.ReachGRC_Trust__B.repository;

import com.example.ReachGRC_Trust__B.entity.Control;
import com.example.ReachGRC_Trust__B.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ControlRepository extends JpaRepository<Control, Long> {

    List<Control> findByDomainId(Long domainId);

    List<Control> findByStatus(Status status);

    boolean existsByDomainIdAndName(Long domainId, String name);
}
