package com.placement.tracker.repository;

import com.placement.tracker.entity.Opportunity;
import com.placement.tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {

    // Opportunities visible to a particular department (student dashboard)
    List<Opportunity> findByAllowedDepartments_Id(Long departmentId);

    // Only opportunities whose last date hasn't passed yet
    List<Opportunity> findByLastDateGreaterThanEqual(LocalDate today);

    List<Opportunity> findByAllowedDepartments_IdAndLastDateGreaterThanEqual(Long departmentId, LocalDate today);

    List<Opportunity> findByCreatedBy(User createdBy);
    @Query("SELECT COUNT(DISTINCT o.companyName) FROM Opportunity o")
    long countDistinctCompanies();
}