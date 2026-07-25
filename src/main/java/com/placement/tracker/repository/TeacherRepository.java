package com.placement.tracker.repository;

import com.placement.tracker.entity.Department;
import com.placement.tracker.entity.Teacher;
import com.placement.tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByUser(User user);

    Optional<Teacher> findByUser_Id(Long userId);

    Optional<Teacher> findByDepartment(Department department);
}