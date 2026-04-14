package com.example.ReachGRC_Trust__B.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "resources",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"file_name"," company_id"}, name = "File Already Exists")
        }
)

public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_data", nullable = false)
    @Lob
    private byte[] fileData;

    @Column(name = "created_at",nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}
