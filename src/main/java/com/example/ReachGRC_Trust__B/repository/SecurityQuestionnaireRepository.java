package com.example.ReachGRC_Trust__B.repository;

import com.example.ReachGRC_Trust__B.entity.SecurityQuestionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecurityQuestionnaireRepository extends JpaRepository<SecurityQuestionnaire, Long> {

    List<SecurityQuestionnaire> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<SecurityQuestionnaire> findAllByOrderByCreatedAtDesc();

    Optional<SecurityQuestionnaire> findByTicketId(String ticketId);
}
