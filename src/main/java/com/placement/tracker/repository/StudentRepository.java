package com.placement.tracker.repository;

import com.placement.tracker.entity.Department;
import com.placement.tracker.entity.Student;
import com.placement.tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUser(User user);

    Optional<Student> findByUser_Id(Long userId);

    List<Student> findByDepartment(Department department);

    List<Student> findByDepartment_Id(Long departmentId);

    long countByDepartment_Id(Long departmentId);
}