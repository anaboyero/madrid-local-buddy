package com.madridlocalbuddy.infrastructure;

import com.madridlocalbuddy.application.RequestRepository;
import com.madridlocalbuddy.domain.ExperienceRequest;
import com.madridlocalbuddy.domain.StoredExperienceRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Repository
class JpaRequestRepository implements RequestRepository {

    private final SpringDataExperienceRequestRepository repository;

    JpaRequestRepository(SpringDataExperienceRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public long save(ExperienceRequest request) {
        ExperienceRequestEntity entity = new ExperienceRequestEntity();
        entity.setExperienceId(request.experience().id());
        entity.setVisitorEmail(request.visitor().email());
        entity.setComment(request.comment());
        entity.setNativeEnglishSpeaker(request.visitor().nativeEnglishSpeaker());
        entity.setCreatedAt(Instant.now());
        return repository.save(entity).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoredExperienceRequest> findAllNewestFirst() {
        return repository.findAllByOrderByCreatedAtDescIdDesc().stream()
                .map(this::toDomain)
                .toList();
    }

    private StoredExperienceRequest toDomain(ExperienceRequestEntity entity) {
        return new StoredExperienceRequest(
                entity.getId(),
                entity.getExperienceId(),
                entity.getVisitorEmail(),
                entity.getComment(),
                entity.isNativeEnglishSpeaker(),
                entity.getCreatedAt());
    }
}
