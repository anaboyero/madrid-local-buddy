package com.madridlocalbuddy.domain;

import com.madridlocalbuddy.support.ContractCatalog;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExperienceCatalogTest {

    private final ExperienceCatalog catalog = new ExperienceCatalog();

    @Test
    void all_returnsExactlyTwoExperiences() {
        assertEquals(2, catalog.all().size());
    }

    @Test
    void all_returnsCinemaFirstThenCasaDeCampoWalk() {
        List<Experience> experiences = catalog.all();

        assertEquals(ContractCatalog.CINEMA, experiences.get(0));
        assertEquals(ContractCatalog.CASA_DE_CAMPO_WALK, experiences.get(1));
    }

    @Test
    void findById_returnsCinemaWhenIdMatches() {
        Optional<Experience> found = catalog.findById(ContractCatalog.CINEMA_ID);

        assertTrue(found.isPresent());
        assertEquals(ContractCatalog.CINEMA, found.get());
    }

    @Test
    void findById_returnsCasaDeCampoWhenIdMatches() {
        Optional<Experience> found = catalog.findById(ContractCatalog.CASA_DE_CAMPO_WALK_ID);

        assertTrue(found.isPresent());
        assertEquals(ContractCatalog.CASA_DE_CAMPO_WALK, found.get());
    }

    @Test
    void findById_returnsEmptyForUnknownId() {
        assertTrue(catalog.findById(999).isEmpty());
    }
}
