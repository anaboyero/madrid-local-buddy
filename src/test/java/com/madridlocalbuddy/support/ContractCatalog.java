package com.madridlocalbuddy.support;

import com.madridlocalbuddy.domain.Experience;

public final class ContractCatalog {

    public static final int CINEMA_ID = 1;
    public static final String CINEMA_TITLE = "Cinema";
    public static final String CINEMA_DESCRIPTION =
            "An evening at a local cinema — film and conversation in English.";

    public static final int CASA_DE_CAMPO_WALK_ID = 2;
    public static final String CASA_DE_CAMPO_WALK_TITLE = "Casa de Campo walk";
    public static final String CASA_DE_CAMPO_WALK_DESCRIPTION =
            "A relaxed walk in Casa de Campo — green Madrid away from the tourist centre.";

    public static final Experience CINEMA = new Experience(CINEMA_ID, CINEMA_TITLE, CINEMA_DESCRIPTION);
    public static final Experience CASA_DE_CAMPO_WALK =
            new Experience(CASA_DE_CAMPO_WALK_ID, CASA_DE_CAMPO_WALK_TITLE, CASA_DE_CAMPO_WALK_DESCRIPTION);

    private ContractCatalog() {
    }
}
