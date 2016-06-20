package lan.dk.podcastserver.manager.worker.updater;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import com.jayway.jsonpath.TypeRef;
import lan.dk.podcastserver.entity.Item;
import lan.dk.podcastserver.entity.Podcast;
import lan.dk.podcastserver.service.ImageService;
import lan.dk.podcastserver.service.JsonService;
import lan.dk.podcastserver.service.UrlService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toSet;

/**
 * Created by kevin on 21/02/2016 for Podcast Server
 */
@Slf4j
@Component("DailymotionUpdater")
public class DailymotionUpdater extends AbstractUpdater {

    static final String API_LIST_OF_ITEMS = "https://api.dailymotion.com/user/%s/videos?fields=created_time,description,id,thumbnail_720_url,title";
    private static final Pattern USER_NAME_EXTRACTOR = Pattern.compile("^.+dailymotion.com\\/(.*)");
    private static final String ITEM_URL = "http://www.dailymotion.com/video/%s";
    private static final TypeRef<List<DailymotionVideoDetail>> LIST_DAILYMOTIONVIDEODETAIL_TYPE = new TypeRef<List<DailymotionVideoDetail>>() { };

    @Autowired JsonService jsonService;
    @Autowired ImageService imageService;
    @Autowired UrlService urlService;

    @Override
    @SuppressWarnings("unchecked")
    public Set<Item> getItems(Podcast podcast) {
        return usernameOf(podcast.getUrl())
                .map(username -> String.format(API_LIST_OF_ITEMS, username))
                .flatMap(urlService::newURL)
                .flatMap(jsonService::parse)
                .map(p -> p.read("list", LIST_DAILYMOTIONVIDEODETAIL_TYPE))
                .map(this::asSet)
                .orElse(Sets.newHashSet());
    }

    private Set<Item> asSet(List<DailymotionVideoDetail> jsonArray) {
        return jsonArray.stream()
                .map(i -> Item.builder()
                        .url(String.format(ITEM_URL, i.getId()))
                        .cover(imageService.getCoverFromURL(i.getCover()))
                        .title(i.getTitle())
                        .pubDate(ZonedDateTime.ofInstant(Instant.ofEpochSecond(i.getCreationDate()), ZoneId.of("Europe/Paris")))
                        .description((i.getDescription()))
                        .build()
                )
                .collect(toSet());
    }

    @Override
    public String signatureOf(Podcast podcast) {
        return usernameOf(podcast.getUrl())
                .map(u -> signatureService.generateSignatureFromURL(String.format(API_LIST_OF_ITEMS, u)))
                .orElseThrow(() -> new RuntimeException("Username not Found"));
    }

    @Override
    public Type type() {
        return new AbstractUpdater.Type("Dailymotion", "Dailymotion");
    }

    @Override
    public Integer compatibility(String url) {
        return StringUtils.contains(url, "www.dailymotion.com") ? 1 : Integer.MAX_VALUE;
    }

    private Optional<String> usernameOf(String url) {
        // http://www.dailymotion.com/karimdebbache
        Matcher matcher = USER_NAME_EXTRACTOR.matcher(url);
        if (matcher.find())
            return Optional.of(matcher.group(1));

        return Optional.empty();
    }

    private static class DailymotionVideoDetail {
        @Getter @Setter private String id;
        @JsonProperty("thumbnail_720_url") @Getter @Setter private String cover;
        @Getter @Setter private String title;
        @JsonProperty("created_time") @Getter @Setter private Long creationDate;
        @Getter @Setter private String description;

    }
}