package com.placement.tracker.service.impl;

import com.placement.tracker.entity.Department;
import com.placement.tracker.exception.ResourceNotFoundException;
import com.placement.tracker.repository.DepartmentRepository;
import com.placement.tracker.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public Department createDepartment(String name, String code) {
        Department department = new Department();
        department.setName(name);
        department.setCode(code.toUpperCase());
        return departmentRepository.save(department);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentByCode(String code) {
        return departmentRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Department", 0L));
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }

    @Override
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department", id);
        }
        departmentRepository.deleteById(id);
    }
}