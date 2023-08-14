/*
 * This file is generated by jOOQ.
 */
package com.github.davinkevin.podcastserver.database.enums;


import com.github.davinkevin.podcastserver.database.Public;

import org.jooq.Catalog;
import org.jooq.EnumType;
import org.jooq.Schema;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public enum DownloadingState implements EnumType {

    WAITING("WAITING"),

    DOWNLOADING("DOWNLOADING");

    private final String literal;

    private DownloadingState(String literal) {
        this.literal = literal;
    }

    @Override
    public Catalog getCatalog() {
        return getSchema().getCatalog();
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public String getName() {
        return "downloading_state";
    }

    @Override
    public String getLiteral() {
        return literal;
    }

    /**
     * Lookup a value of this EnumType by its literal
     */
    public static DownloadingState lookupLiteral(String literal) {
        return EnumType.lookupLiteral(DownloadingState.class, literal);
    }
}
