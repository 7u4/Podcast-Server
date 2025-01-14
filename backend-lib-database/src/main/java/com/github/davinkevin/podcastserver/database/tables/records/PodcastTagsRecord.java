/*
 * This file is generated by jOOQ.
 */
package com.github.davinkevin.podcastserver.database.tables.records;


import com.github.davinkevin.podcastserver.database.tables.PodcastTags;

import java.util.UUID;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PodcastTagsRecord extends UpdatableRecordImpl<PodcastTagsRecord> implements Record2<UUID, UUID> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.podcast_tags.podcasts_id</code>.
     */
    public void setPodcastsId(UUID value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.podcast_tags.podcasts_id</code>.
     */
    public UUID getPodcastsId() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>public.podcast_tags.tags_id</code>.
     */
    public void setTagsId(UUID value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.podcast_tags.tags_id</code>.
     */
    public UUID getTagsId() {
        return (UUID) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<UUID, UUID> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<UUID, UUID> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<UUID, UUID> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<UUID> field1() {
        return PodcastTags.PODCAST_TAGS.PODCASTS_ID;
    }

    @Override
    public Field<UUID> field2() {
        return PodcastTags.PODCAST_TAGS.TAGS_ID;
    }

    @Override
    public UUID component1() {
        return getPodcastsId();
    }

    @Override
    public UUID component2() {
        return getTagsId();
    }

    @Override
    public UUID value1() {
        return getPodcastsId();
    }

    @Override
    public UUID value2() {
        return getTagsId();
    }

    @Override
    public PodcastTagsRecord value1(UUID value) {
        setPodcastsId(value);
        return this;
    }

    @Override
    public PodcastTagsRecord value2(UUID value) {
        setTagsId(value);
        return this;
    }

    @Override
    public PodcastTagsRecord values(UUID value1, UUID value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PodcastTagsRecord
     */
    public PodcastTagsRecord() {
        super(PodcastTags.PODCAST_TAGS);
    }

    /**
     * Create a detached, initialised PodcastTagsRecord
     */
    public PodcastTagsRecord(UUID podcastsId, UUID tagsId) {
        super(PodcastTags.PODCAST_TAGS);

        setPodcastsId(podcastsId);
        setTagsId(tagsId);
        resetChangedOnNotNull();
    }
}
