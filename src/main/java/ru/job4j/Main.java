package ru.job4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.grabber.service.*;
import ru.job4j.grabber.stores.*;
import ru.job4j.grabber.model.Post;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        var config = new Config();
        config.load("application.properties");
        try (var connection = DriverManager.getConnection(config.get("db.url"),
                config.get("db.username"),
                config.get("db.password"))) {
            Store store = new JdbcStore(connection);

//            // Добавляем тестовый пост
//            var post = new Post();
//            post.setTitle("Super Java Job");
//            store.save(post);

            // Настраиваем и запускаем планировщик
            var scheduler = new SchedulerManager();
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store
            );

            // Запускаем веб-сервер
            new Web(store).start(Integer.parseInt(config.get("server.port")));
        } catch (SQLException e) {
            log.error("When creating a connection", e);
        }
    }
}
