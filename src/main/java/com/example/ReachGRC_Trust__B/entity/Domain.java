package com.example.ReachGRC_Trust__B.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "domain")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Domain {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;


    @OneToMany(mappedBy = "domain", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Control> controls = new ArrayList<>();


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    //helper

    public void addControl(Control control){
        controls.add(control);
        control.setDomain(this);
    }

    public void removeControl(Control control){
        controls.remove(control);
        control.setDomain(null);
    }

//    @Override
//    public String toString() {
//        return "Domain{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", companyId=" + (company != null ? company.getId() : null) +
//                '}';
//    }
}
