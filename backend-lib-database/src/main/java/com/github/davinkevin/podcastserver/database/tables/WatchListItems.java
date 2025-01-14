/*
 * This file is generated by jOOQ.
 */
package com.github.davinkevin.podcastserver.database.tables;


import com.github.davinkevin.podcastserver.database.Keys;
import com.github.davinkevin.podcastserver.database.Public;
import com.github.davinkevin.podcastserver.database.tables.records.WatchListItemsRecord;

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
public class WatchListItems extends TableImpl<WatchListItemsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.watch_list_items</code>
     */
    public static final WatchListItems WATCH_LIST_ITEMS = new WatchListItems();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WatchListItemsRecord> getRecordType() {
        return WatchListItemsRecord.class;
    }

    /**
     * The column <code>public.watch_list_items.watch_lists_id</code>.
     */
    public final TableField<WatchListItemsRecord, UUID> WATCH_LISTS_ID = createField(DSL.name("watch_lists_id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>public.watch_list_items.items_id</code>.
     */
    public final TableField<WatchListItemsRecord, UUID> ITEMS_ID = createField(DSL.name("items_id"), SQLDataType.UUID.nullable(false), this, "");

    private WatchListItems(Name alias, Table<WatchListItemsRecord> aliased) {
        this(alias, aliased, null);
    }

    private WatchListItems(Name alias, Table<WatchListItemsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.watch_list_items</code> table reference
     */
    public WatchListItems(String alias) {
        this(DSL.name(alias), WATCH_LIST_ITEMS);
    }

    /**
     * Create an aliased <code>public.watch_list_items</code> table reference
     */
    public WatchListItems(Name alias) {
        this(alias, WATCH_LIST_ITEMS);
    }

    /**
     * Create a <code>public.watch_list_items</code> table reference
     */
    public WatchListItems() {
        this(DSL.name("watch_list_items"), null);
    }

    public <O extends Record> WatchListItems(Table<O> child, ForeignKey<O, WatchListItemsRecord> key) {
        super(child, key, WATCH_LIST_ITEMS);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<WatchListItemsRecord> getPrimaryKey() {
        return Keys.WATCH_LIST_ITEMS_PKEY;
    }

    @Override
    public List<ForeignKey<WatchListItemsRecord, ?>> getReferences() {
        return Arrays.asList(Keys.WATCH_LIST_ITEMS__WATCH_LIST_ITEMS_WATCH_LISTS_ID_FKEY, Keys.WATCH_LIST_ITEMS__WATCH_LIST_ITEMS_ITEMS_ID_FKEY);
    }

    private transient WatchList _watchList;
    private transient Item _item;

    /**
     * Get the implicit join path to the <code>public.watch_list</code> table.
     */
    public WatchList watchList() {
        if (_watchList == null)
            _watchList = new WatchList(this, Keys.WATCH_LIST_ITEMS__WATCH_LIST_ITEMS_WATCH_LISTS_ID_FKEY);

        return _watchList;
    }

    /**
     * Get the implicit join path to the <code>public.item</code> table.
     */
    public Item item() {
        if (_item == null)
            _item = new Item(this, Keys.WATCH_LIST_ITEMS__WATCH_LIST_ITEMS_ITEMS_ID_FKEY);

        return _item;
    }

    @Override
    public WatchListItems as(String alias) {
        return new WatchListItems(DSL.name(alias), this);
    }

    @Override
    public WatchListItems as(Name alias) {
        return new WatchListItems(alias, this);
    }

    @Override
    public WatchListItems as(Table<?> alias) {
        return new WatchListItems(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public WatchListItems rename(String name) {
        return new WatchListItems(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WatchListItems rename(Name name) {
        return new WatchListItems(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public WatchListItems rename(Table<?> name) {
        return new WatchListItems(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<UUID, UUID> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function2<? super UUID, ? super UUID, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function2<? super UUID, ? super UUID, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
