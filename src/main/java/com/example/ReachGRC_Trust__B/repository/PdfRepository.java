package com.example.ReachGRC_Trust__B.repository;

import com.example.ReachGRC_Trust__B.entity.Pdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdfRepository extends JpaRepository<Pdf, Long> {
    Pdf findByPdfId(Long pdfId);
    boolean existsByPdfId(Long pdfId);
    boolean existsByFileName(String fileName);
    List<Pdf> findByCompanyId(Long companyId);
    void deleteByPdfId(Long pdfId);
    void deleteByCompanyId(Long companyId);
}
