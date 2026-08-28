package com.example.ReachGRC_Trust__B.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "system_services")
@Getter
@Setter
@ToString(exclude = {"group", "incidents"})
@EqualsAndHashCode(exclude = {"group", "incidents"})
@AllArgsConstructor
@NoArgsConstructor
public class SystemService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String status = "operational"; // operational, degraded, outage

    @Column(nullable = false)
    private Double uptime = 99.99;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @JsonBackReference
    private SystemStatusGroup group;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    @JsonManagedReference
    private List<StatusIncident> incidents = new ArrayList<>();

    public void addIncident(StatusIncident incident) {
        incidents.add(incident);
        incident.setService(this);
    }

    public void removeIncident(StatusIncident incident) {
        incidents.remove(incident);
        incident.setService(null);
    }
}
