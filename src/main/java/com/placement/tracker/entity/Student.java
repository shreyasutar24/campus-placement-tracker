package com.placement.tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity                          // This class = database table
@Table(name = "students")        // Table name = "students"
@Data                            // Auto generate getters & setters
@NoArgsConstructor               // Empty constructor for Hibernate
@AllArgsConstructor              // Full constructor for developer
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Link back to the pre-approved record used during registration
    @OneToOne
    @JoinColumn(name = "student_record_id", nullable = false, unique = true)
    private StudentRecord studentRecord;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    private Double cgpa;

    @Column(name = "tenth_percentage")
    private Double tenthPercentage;

    @Column(name = "twelfth_percentage")
    private Double twelfthPercentage;

    @Column(name = "has_active_backlog")
    private boolean hasActiveBacklog = false;

    @Column(length = 1000)
    private String skills; // comma separated, e.g. "Java,SQL,React"

    private String resumeFileName;
}