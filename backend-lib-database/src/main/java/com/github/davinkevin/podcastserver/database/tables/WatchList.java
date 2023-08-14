/*
 * This file is generated by jOOQ.
 */
package com.github.davinkevin.podcastserver.database.tables;


import com.github.davinkevin.podcastserver.database.Keys;
import com.github.davinkevin.podcastserver.database.Public;
import com.github.davinkevin.podcastserver.database.tables.records.WatchListRecord;

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
public class WatchList extends TableImpl<WatchListRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.watch_list</code>
     */
    public static final WatchList WATCH_LIST = new WatchList();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WatchListRecord> getRecordType() {
        return WatchListRecord.class;
    }

    /**
     * The column <code>public.watch_list.id</code>.
     */
    public final TableField<WatchListRecord, UUID> ID = createField(DSL.name("id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>public.watch_list.name</code>.
     */
    public final TableField<WatchListRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(255), this, "");

    private WatchList(Name alias, Table<WatchListRecord> aliased) {
        this(alias, aliased, null);
    }

    private WatchList(Name alias, Table<WatchListRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.watch_list</code> table reference
     */
    public WatchList(String alias) {
        this(DSL.name(alias), WATCH_LIST);
    }

    /**
     * Create an aliased <code>public.watch_list</code> table reference
     */
    public WatchList(Name alias) {
        this(alias, WATCH_LIST);
    }

    /**
     * Create a <code>public.watch_list</code> table reference
     */
    public WatchList() {
        this(DSL.name("watch_list"), null);
    }

    public <O extends Record> WatchList(Table<O> child, ForeignKey<O, WatchListRecord> key) {
        super(child, key, WATCH_LIST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<WatchListRecord> getPrimaryKey() {
        return Keys.WATCH_LIST_PKEY;
    }

    @Override
    public List<UniqueKey<WatchListRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.WATCH_LIST_NAME_KEY);
    }

    @Override
    public WatchList as(String alias) {
        return new WatchList(DSL.name(alias), this);
    }

    @Override
    public WatchList as(Name alias) {
        return new WatchList(alias, this);
    }

    @Override
    public WatchList as(Table<?> alias) {
        return new WatchList(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public WatchList rename(String name) {
        return new WatchList(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WatchList rename(Name name) {
        return new WatchList(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public WatchList rename(Table<?> name) {
        return new WatchList(name.getQualifiedName(), null);
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
