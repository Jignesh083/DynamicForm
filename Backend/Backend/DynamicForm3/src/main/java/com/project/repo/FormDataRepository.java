package com.project.repo;


import org.springframework.data.jpa.repository.JpaRepository;

import com.project.model.FormData;


public interface FormDataRepository extends JpaRepository<FormData, Long> {
}
