package com.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.model.EducationDetails;

public interface EducationDetailsRepository extends JpaRepository<EducationDetails, Long> {
}
