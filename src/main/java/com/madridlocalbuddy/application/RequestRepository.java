package com.madridlocalbuddy.application;

import com.madridlocalbuddy.domain.ExperienceRequest;
import com.madridlocalbuddy.domain.StoredExperienceRequest;

import java.util.List;

public interface RequestRepository {

    long save(ExperienceRequest request);

    List<StoredExperienceRequest> findAllNewestFirst();
}
