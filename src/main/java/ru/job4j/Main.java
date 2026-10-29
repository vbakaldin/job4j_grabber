package ru.job4j;

import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.service.Config;
import ru.job4j.grabber.service.HabrCareerParse;
import ru.job4j.grabber.service.SchedulerManager;
import ru.job4j.grabber.service.SuperJobGrab;
import ru.job4j.grabber.stores.JdbcStore;
import ru.job4j.grabber.stores.MemStore;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        var config = new Config();
        config.load("application.properties");

        try (var connection = DriverManager.getConnection(
                config.get("url"), config.get("username"), config.get("password"));
            var scheduler = new SchedulerManager()) {
            var store = new JdbcStore(connection);
            var post = new Post();
            post.setTitle("Super Java Job");
            post.setDescription("Example Java vacancy");
            post.setLink("https://example.com/vacancies/java");
            post.setTime(System.currentTimeMillis());
            store.save(post);

            scheduler.init();
            scheduler.load(Integer.parseInt(config.get("rabbit.interval")), SuperJobGrab.class, store);
            Thread.sleep(10000);
        } catch (SQLException e) {
            LOG.error("When create a connection", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}