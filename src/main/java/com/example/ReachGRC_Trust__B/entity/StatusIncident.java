package com.example.ReachGRC_Trust__B.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "status_incidents")
@Getter
@Setter
@ToString(exclude = "service")
@EqualsAndHashCode(exclude = "service")
@AllArgsConstructor
@NoArgsConstructor
public class StatusIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String type = "yellow"; // yellow, red

    @Column(columnDefinition = "TEXT", nullable = false)
    private String msg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    @JsonBackReference
    private SystemService service;
}
