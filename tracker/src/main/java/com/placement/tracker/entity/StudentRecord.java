package com.placement.tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pre-approved list of valid students for this college.
 * Admin/Placement Officer loads this table BEFORE students can register.
 * Used to verify Roll Number + University PRN at registration time,
 * so that outside students cannot self-register.
 */
@Entity
@Table(name = "student_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "roll_number", nullable = false, unique = true)
    private String rollNumber;

    @Column(name = "university_number", nullable = false, unique = true)
    private String universityNumber; // PRN / Enrollment Number

    @Column(nullable = false)
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false)
    private Integer passingYear;

    private String email; // optional

    @Column(nullable = false)
    private boolean isRegistered = false; // becomes true once student completes signup
}