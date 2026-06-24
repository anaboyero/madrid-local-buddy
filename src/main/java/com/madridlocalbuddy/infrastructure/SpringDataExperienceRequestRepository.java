package com.madridlocalbuddy.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface SpringDataExperienceRequestRepository extends JpaRepository<ExperienceRequestEntity, Long> {

    List<ExperienceRequestEntity> findAllByOrderByCreatedAtDescIdDesc();
}
