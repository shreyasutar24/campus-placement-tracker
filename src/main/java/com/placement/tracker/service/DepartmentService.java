package com.placement.tracker.service;

import com.placement.tracker.entity.Department;

import java.util.List;

public interface DepartmentService {

    Department createDepartment(String name, String code);

    List<Department> getAllDepartments();

    Department getDepartmentByCode(String code);

    Department getDepartmentById(Long id);

    void deleteDepartment(Long id);
}