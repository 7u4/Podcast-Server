/*
 * This file is generated by jOOQ.
 */
package com.github.davinkevin.podcastserver.database.tables;


import com.github.davinkevin.podcastserver.database.Keys;
import com.github.davinkevin.podcastserver.database.Public;
import com.github.davinkevin.podcastserver.database.tables.records.PodcastTagsRecord;

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
public class PodcastTags extends TableImpl<PodcastTagsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.podcast_tags</code>
     */
    public static final PodcastTags PODCAST_TAGS = new PodcastTags();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PodcastTagsRecord> getRecordType() {
        return PodcastTagsRecord.class;
    }

    /**
     * The column <code>public.podcast_tags.podcasts_id</code>.
     */
    public final TableField<PodcastTagsRecord, UUID> PODCASTS_ID = createField(DSL.name("podcasts_id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>public.podcast_tags.tags_id</code>.
     */
    public final TableField<PodcastTagsRecord, UUID> TAGS_ID = createField(DSL.name("tags_id"), SQLDataType.UUID.nullable(false), this, "");

    private PodcastTags(Name alias, Table<PodcastTagsRecord> aliased) {
        this(alias, aliased, null);
    }

    private PodcastTags(Name alias, Table<PodcastTagsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.podcast_tags</code> table reference
     */
    public PodcastTags(String alias) {
        this(DSL.name(alias), PODCAST_TAGS);
    }

    /**
     * Create an aliased <code>public.podcast_tags</code> table reference
     */
    public PodcastTags(Name alias) {
        this(alias, PODCAST_TAGS);
    }

    /**
     * Create a <code>public.podcast_tags</code> table reference
     */
    public PodcastTags() {
        this(DSL.name("podcast_tags"), null);
    }

    public <O extends Record> PodcastTags(Table<O> child, ForeignKey<O, PodcastTagsRecord> key) {
        super(child, key, PODCAST_TAGS);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<PodcastTagsRecord> getPrimaryKey() {
        return Keys.PODCAST_TAGS_PKEY;
    }

    @Override
    public List<ForeignKey<PodcastTagsRecord, ?>> getReferences() {
        return Arrays.asList(Keys.PODCAST_TAGS__PODCAST_TAGS_PODCASTS_ID_FKEY, Keys.PODCAST_TAGS__PODCAST_TAGS_TAGS_ID_FKEY);
    }

    private transient Podcast _podcast;
    private transient Tag _tag;

    /**
     * Get the implicit join path to the <code>public.podcast</code> table.
     */
    public Podcast podcast() {
        if (_podcast == null)
            _podcast = new Podcast(this, Keys.PODCAST_TAGS__PODCAST_TAGS_PODCASTS_ID_FKEY);

        return _podcast;
    }

    /**
     * Get the implicit join path to the <code>public.tag</code> table.
     */
    public Tag tag() {
        if (_tag == null)
            _tag = new Tag(this, Keys.PODCAST_TAGS__PODCAST_TAGS_TAGS_ID_FKEY);

        return _tag;
    }

    @Override
    public PodcastTags as(String alias) {
        return new PodcastTags(DSL.name(alias), this);
    }

    @Override
    public PodcastTags as(Name alias) {
        return new PodcastTags(alias, this);
    }

    @Override
    public PodcastTags as(Table<?> alias) {
        return new PodcastTags(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public PodcastTags rename(String name) {
        return new PodcastTags(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PodcastTags rename(Name name) {
        return new PodcastTags(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public PodcastTags rename(Table<?> name) {
        return new PodcastTags(name.getQualifiedName(), null);
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
