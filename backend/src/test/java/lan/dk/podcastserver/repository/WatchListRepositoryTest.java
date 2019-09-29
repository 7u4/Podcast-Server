package lan.dk.podcastserver.repository;

import com.github.davinkevin.podcastserver.entity.Item;
import com.github.davinkevin.podcastserver.entity.WatchList;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import io.vavr.collection.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.util.UUID;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static java.time.ZonedDateTime.now;
import static lan.dk.podcastserver.repository.DatabaseConfigurationTest.DELETE_ALL;
import static lan.dk.podcastserver.repository.DatabaseConfigurationTest.INSERT_ITEM_DATA;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by kevin on 17/01/2016 for PodcastServer
 */
@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import(DatabaseConfigurationTest.class)
public class WatchListRepositoryTest {

    @Autowired DataSource dataSource;
    @Autowired WatchListRepository watchListRepository;
    @Autowired ItemRepository itemRepository;

    private final static DbSetupTracker dbSetupTracker = new DbSetupTracker();
    private static final Operation INSERT_PLAYLIST_DATA = sequenceOf(
            INSERT_ITEM_DATA,
            insertInto("WATCH_LIST")
                    .columns("ID", "NAME")
                    .values(UUID.fromString("dc024a30-bd02-11e5-a837-0800200c9a66"), "Humour Playlist")
                    .values(UUID.fromString("24248480-bd04-11e5-a837-0800200c9a66"), "Conférence Rewind")
                    .build(),
            insertInto("WATCH_LIST_ITEMS")
                    .columns("WATCH_LISTS_ID", "ITEMS_ID")
                    .values(UUID.fromString("dc024a30-bd02-11e5-a837-0800200c9a66"), UUID.fromString("43fb990f-0b5e-413f-920c-6de217f9ecdd"))
                    .values(UUID.fromString("dc024a30-bd02-11e5-a837-0800200c9a66"), UUID.fromString("0a774611-c857-44df-b7e0-5e5af31f7b56"))
                    .values(UUID.fromString("24248480-bd04-11e5-a837-0800200c9a66"), UUID.fromString("0a774611-c857-44df-b7e0-5e5af31f7b56"))
                    .build()
    );

    @BeforeEach
    public void prepare() throws Exception {
        Operation operation = sequenceOf(DELETE_ALL, INSERT_PLAYLIST_DATA);
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);

        dbSetupTracker.launchIfNecessary(dbSetup);
    }

    @Test
    public void should_save_a_playlist() {
        /* Given */
        WatchList watchList = new WatchList();
        watchList.setName("A New Playlist");

        /* When */
        WatchList savedWatchList = watchListRepository.save(watchList);

        /* Then */
        assertThat(savedWatchList.getId()).isNotNull();
    }

    @Test
    public void should_find_by_id() {
        /* Given */
        dbSetupTracker.skipNextLaunch();
        UUID id = UUID.fromString("dc024a30-bd02-11e5-a837-0800200c9a66");

        /* When */
        WatchList watchList = watchListRepository.findById(id).get();

        /* Then */
        assertThat(watchList).isNotNull();
        assertThat(watchList.getItems()).hasSize(2);
        assertThat(watchList.getId()).isEqualTo(id);
        assertThat(watchList.getName()).isEqualTo("Humour Playlist");
    }

    @Test
    public void should_find_playlist_having_item() {
        /* Given */
        dbSetupTracker.skipNextLaunch();
        Item item = new Item(); item.setId(UUID.fromString("0a774611-c857-44df-b7e0-5e5af31f7b56"));

        /* When */
        Set<WatchList> watchLists = watchListRepository.findContainsItem(item);

        /* Then */
        assertThat(watchLists).hasSize(2);
    }

    @Test
    public void should_add_a_item_to_playlist() {
        /* Given */
        Item item = itemRepository.findById(UUID.fromString("b721a6b6-896a-48fc-b820-28aeafddbb53")).get();
        WatchList watchList = watchListRepository.findById(UUID.fromString("24248480-bd04-11e5-a837-0800200c9a66")).get();
        watchList.add(item);
        /* When */
        watchListRepository.save(watchList);
        watchListRepository.flush();
        WatchList fetchedWatchList = watchListRepository.findById(UUID.fromString("24248480-bd04-11e5-a837-0800200c9a66")).get();

        /* Then */
        assertThat(fetchedWatchList.getItems()).hasSize(2);
    }

    @Test
    public void should_remove_item_from_playlist() {
        /* Given */
        Item thirdItem = itemRepository.findById(UUID.fromString("43fb990f-0b5e-413f-920c-6de217f9ecdd")).get();
        WatchList watchList = watchListRepository.findById(UUID.fromString("dc024a30-bd02-11e5-a837-0800200c9a66")).get();
        watchList.remove(thirdItem);
        /* When */
        watchListRepository.save(watchList);
        watchListRepository.flush();
        WatchList fetchedWatchList = watchListRepository.findById(UUID.fromString("dc024a30-bd02-11e5-a837-0800200c9a66")).get();

        /* Then */
        assertThat(fetchedWatchList.getItems()).hasSize(1);
    }

    @Test
    public void should_remove_from_playlist_if_item_deleted() {
        /* Given */

        /* When */
        itemRepository.deleteById(UUID.fromString("43fb990f-0b5e-413f-920c-6de217f9ecdd"));
        itemRepository.flush();
        WatchList fetchedWatchList = watchListRepository.findById(UUID.fromString("dc024a30-bd02-11e5-a837-0800200c9a66")).get();

        /* Then */
        assertThat(fetchedWatchList.getItems()).hasSize(1);
    }
}
