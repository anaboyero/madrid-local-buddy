package com.madridlocalbuddy.api;

import com.madridlocalbuddy.domain.Experience;
import com.madridlocalbuddy.domain.ExperienceCatalog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/experiences")
public class ExperiencesController {

    private final ExperienceCatalog catalog;

    public ExperiencesController(ExperienceCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping
    public List<Experience> listExperiences() {
        return catalog.all();
    }
}
