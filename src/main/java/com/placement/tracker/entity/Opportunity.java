package com.placement.tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "opportunities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String jobRole;

    private Double packageLpa;

    private String location;

    
    @Column(nullable = false, length = 1000)
    private String applyLink;

    @Column(nullable = false)
    private LocalDate lastDate;

    // ---- Eligibility criteria ----
    private Double minCgpa;
    private Double min10thPercentage;
    private Double min12thPercentage;

    @Column(nullable = false)
    private boolean noActiveBacklogAllowed = true;

    @Column(length = 1000)
    private String requiredSkills; 

    @Column(length = 2000)
    private String description;

    
    @ManyToMany
    @JoinTable(
        name = "opportunity_departments",
        joinColumns = @JoinColumn(name = "opportunity_id"),
        inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<Department> allowedDepartments = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}