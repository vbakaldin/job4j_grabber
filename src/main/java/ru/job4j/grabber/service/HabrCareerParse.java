package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import ru.job4j.grabber.model.Post;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import ru.job4j.grabber.utils.DateTimeParser;

public class HabrCareerParse implements Parse {
    private static final Logger log = Logger.getLogger(HabrCareerParse.class);
    private static final String SOURCE_LINK = "https://career.habr.com";
//    private static final String PREFIX = "/vacancies?page=";
//    private static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final int PAGE_COUNT = 5;
    private final DateTimeParser dateTimeParser;

    @Override
    public List<Post> fetch(String link) {
        var result = new ArrayList<Post>();
        try {
            for (int pageNumber = 1; pageNumber <= PAGE_COUNT; pageNumber++) {
//                String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
                String fullLink = link + pageNumber;
                var connection = Jsoup.connect(fullLink);
                var document = connection.get();
                var rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    var titleElement = row.select(".vacancy-card__title").first();
                    var linkElement = titleElement.child(0);
                    String vacancyName = titleElement.text();
                    String vacancyLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                    var dateElement = row.select(".vacancy-card__date").first().child(0);
//                    Long time = OffsetDateTime.parse(dateElement.attr("datetime"))
//                            .toInstant()
//                            .toEpochMilli();
                    String datetime = dateElement.attr("datetime");
                    long seconds = dateTimeParser.parse(datetime).toEpochSecond(ZoneOffset.UTC);
                    var post = new Post();
                    post.setTitle(vacancyName);
                    post.setLink(vacancyLink);
                    post.setDescription(retrieveDescription(vacancyLink));
                    post.setTime(seconds);
                    result.add(post);
                });
            }
        } catch (IOException e) {
            log.error("When load page", e);
        }
        return result;
    }

    private String retrieveDescription(String link) {
        try {
            var document = Jsoup.connect(link).get();
            return document.select(".vacancy-description__text").text();
        } catch (IOException e) {
            log.error("When load description: " + link, e);
            return "";
        }
    }

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }
}