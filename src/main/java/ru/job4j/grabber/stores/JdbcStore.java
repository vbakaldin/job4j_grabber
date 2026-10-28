package ru.job4j.grabber.stores;

import ru.job4j.grabber.model.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcStore implements Store {
    private final Connection connection;

    public JdbcStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Post post) {
        String sql = "INSERT INTO post (name, text, link, created) VALUES (?, ?, ?, ?)";
        try (var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, new Timestamp(post.getTime()));
            if (statement.executeUpdate() > 0) {
                try (var keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        post.setId(keys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot save post", e);
        }
    }

    private Post readPost(ResultSet result) throws SQLException {
        Post post = new Post();
        post.setId(result.getLong("id"));
        post.setTitle(result.getString("name"));
        post.setDescription(result.getString("text"));
        post.setLink(result.getString("link"));
        post.setTime(result.getTimestamp("created").getTime());
        return post;
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (var statement = connection.prepareStatement("SELECT * FROM post ORDER BY id");
             var result = statement.executeQuery()) {
            while (result.next()) {
                posts.add(readPost(result));
            }
            return posts;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read posts", e);
        }
    }

    @Override
    public Optional<Post> findById(Long id) {
        try (var statement = connection.prepareStatement("SELECT * FROM post WHERE id = ?")) {
            statement.setLong(1, id);
            try (var result = statement.executeQuery()) {
                return result.next() ? Optional.of(readPost(result)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot find post: " + id, e);
        }
    }
}