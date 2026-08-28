package com.example.ReachGRC_Trust__B.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "system_status_groups")
@Getter
@Setter
@ToString(exclude = "services")
@EqualsAndHashCode(exclude = "services")
@AllArgsConstructor
@NoArgsConstructor
public class SystemStatusGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @Column(name = "order_index")
    private Integer orderIndex;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    @JsonManagedReference
    private List<SystemService> services = new ArrayList<>();

    public void addService(SystemService service) {
        services.add(service);
        service.setGroup(this);
    }

    public void removeService(SystemService service) {
        services.remove(service);
        service.setGroup(null);
    }
}
