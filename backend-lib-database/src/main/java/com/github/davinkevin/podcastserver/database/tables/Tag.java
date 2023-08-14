/*
 * This file is generated by jOOQ.
 */
package com.github.davinkevin.podcastserver.database.tables;


import com.github.davinkevin.podcastserver.database.Keys;
import com.github.davinkevin.podcastserver.database.Public;
import com.github.davinkevin.podcastserver.database.tables.records.TagRecord;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function2;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tag extends TableImpl<TagRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.tag</code>
     */
    public static final Tag TAG = new Tag();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TagRecord> getRecordType() {
        return TagRecord.class;
    }

    /**
     * The column <code>public.tag.id</code>.
     */
    public final TableField<TagRecord, UUID> ID = createField(DSL.name("id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>public.tag.name</code>.
     */
    public final TableField<TagRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(255), this, "");

    private Tag(Name alias, Table<TagRecord> aliased) {
        this(alias, aliased, null);
    }

    private Tag(Name alias, Table<TagRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.tag</code> table reference
     */
    public Tag(String alias) {
        this(DSL.name(alias), TAG);
    }

    /**
     * Create an aliased <code>public.tag</code> table reference
     */
    public Tag(Name alias) {
        this(alias, TAG);
    }

    /**
     * Create a <code>public.tag</code> table reference
     */
    public Tag() {
        this(DSL.name("tag"), null);
    }

    public <O extends Record> Tag(Table<O> child, ForeignKey<O, TagRecord> key) {
        super(child, key, TAG);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<TagRecord> getPrimaryKey() {
        return Keys.TAG_PKEY;
    }

    @Override
    public List<UniqueKey<TagRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.TAG_NAME_KEY);
    }

    @Override
    public Tag as(String alias) {
        return new Tag(DSL.name(alias), this);
    }

    @Override
    public Tag as(Name alias) {
        return new Tag(alias, this);
    }

    @Override
    public Tag as(Table<?> alias) {
        return new Tag(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Tag rename(String name) {
        return new Tag(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Tag rename(Name name) {
        return new Tag(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Tag rename(Table<?> name) {
        return new Tag(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<UUID, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function2<? super UUID, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function2<? super UUID, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
