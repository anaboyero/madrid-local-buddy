package com.madridlocalbuddy.domain;

import java.util.List;
import java.util.Optional;

public class ExperienceCatalog {

    private static final List<Experience> EXPERIENCES = List.of(
            new Experience(
                    1,
                    "Cinema",
                    "An evening at a local cinema — film and conversation in English."),
            new Experience(
                    2,
                    "Casa de Campo walk",
                    "A relaxed walk in Casa de Campo — green Madrid away from the tourist centre."));

    public List<Experience> all() {
        return EXPERIENCES;
    }

    public Optional<Experience> findById(int id) {
        return EXPERIENCES.stream()
                .filter(experience -> experience.id() == id)
                .findFirst();
    }
}
