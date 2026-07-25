package com.placement.tracker.repository;

import com.placement.tracker.entity.Application;
import com.placement.tracker.entity.ApplicationStatus;
import com.placement.tracker.entity.Opportunity;
import com.placement.tracker.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudent(Student student);
    List<Application> findByStudent_Id(Long studentId);
    List<Application> findByOpportunity(Opportunity opportunity);
    List<Application> findByOpportunity_Id(Long opportunityId);

    // Prevents a student from applying twice to the same opportunity
    Optional<Application> findByStudentAndOpportunity(Student student, Opportunity opportunity);
    boolean existsByStudentAndOpportunity(Student student, Opportunity opportunity);

    List<Application> findByStatus(ApplicationStatus status);
    long countByOpportunity_IdAndStatus(Long opportunityId, ApplicationStatus status);

    // For admin verification screen - student-claimed SELECTED status not yet verified
    List<Application> findByStatusAndVerifiedByAdminFalse(ApplicationStatus status);

    // Count of distinct students who have at least one SELECTED application
    @Query("SELECT COUNT(DISTINCT a.student.id) FROM Application a WHERE a.status = ApplicationStatus.SELECTED")
    long countDistinctPlacedStudents();
}