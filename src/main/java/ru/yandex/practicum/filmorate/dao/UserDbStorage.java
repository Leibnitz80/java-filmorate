package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.ActionType;
import ru.yandex.practicum.filmorate.model.enums.ObjectType;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        String sql = "select user_id, login, name, email, birthday from Users;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User addUser(User user) {
        String sql = "insert into Users(login, name, email, birthday)" +
                     "values(?,?,?,?);";
        jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        sql = "select user_id as id, login, name, email, birthday from Users where login = ?;";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), user.getLogin());
    }

    @Override
    public void updateUser(User user) {
        checkUserContains(user.getId());
        String sql = "update Users " +
                     "set login = ?," +
                     "name = ?," +
                     "email = ?," +
                     "birthday = ?" +
                     "where user_id = ?;";
        jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        checkUserContains(id);
        String sql = "delete from Users where user_id = ?;";
        jdbcTemplate.update(sql, id);

        // Удаляю все записи с user_id из friendship
        deleteUserFromFriendship(id);
    }

    @Override
    public User getUserById(Long id) {
        checkUserContains(id);
        String sql = "select user_id, login, name, email, birthday from Users where user_id = ?;";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public void makeFriends(Long userId, Long friendId) {
        checkUserContains(userId);
        checkUserContains(friendId);
        String sql = "insert into Friendship(user_id, friend_id) " +
                     "values(?,?);";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFriends(Long friendId, Long userId) {
        String sql = "delete from Friendship where user_id in (?,?) and friend_id in (?,?);";
        jdbcTemplate.update(sql, userId, friendId, userId, friendId);
    }

    @Override
    public List<User> getAllFriends(Long id) {
        String sql = "select u.user_id, u.login, u.name, u.email, u.birthday " +
                     "from Friendship f " +
                     "inner join Users u on u.user_id = f.friend_id " +
                     "where f.user_id = ? " +
                     "order by u.user_id;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public List<User> getCommonFriends(Long id1, Long id2) {
        String sql = "select u.user_id, u.login, u.name, u.email, u.birthday " +
                     "from Friendship f1 " +
                     "inner join Friendship f2 on f2.friend_id = f1.friend_id " +
                     "inner join Users u on u.user_id = f2.friend_id " +
                     "where f1.user_id = ? and f2.user_id = ? " +
                     "and f1.friend_id <> f2.user_id and f2.friend_id <> f1.user_id;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id1, id2);
    }

    @Override
    public List<Event> getUserEvents(Long userId) {
        String sql = "select event_id, eventtimestamp, user_id, eventtype, operation, entity_id " +
                "from Events " +
                "where user_id = ? " +
                "order by eventtimestamp";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), userId);
    }

    @Override
    public void addUserEvent(Long userId, String eventType, String operation, Long entityId) {
        String sql = "insert into Events(eventtimestamp, user_id, eventtype, operation, entity_id) " +
                     "values(?,?,?,?,?);";
        jdbcTemplate.update(sql, new Date().getTime(), userId, eventType, operation, entityId);
    }

    @Override
    public void deleteUserEvents(Long userId) {
        String sql = "delete from Events where user_id = ?;";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public void checkUserContains(Long id) {
        log.info("Валидация checkUserContains id={}", id);
        String sql = "select count(1) as row_count from Users where user_id = ?;";
        Long rowCount = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong("row_count"), id);
        if (rowCount == 0) {
            String message = String.format("Пользователь c id= %d не найден!", id);
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    // Удаление всех записей, связанных с удаляемым пользователем -
    // из таблицы friendship
    private void deleteUserFromFriendship(Long id) {
        String sql = "delete from Friendship where user_id = ? or friend_id = ?";
        jdbcTemplate.update(sql, id, id);
        log.info("Все записи с user_id {} удалены из таблицы Friendship", id);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        // используем конструктор, методы ResultSet
        // и готовое значение user
        Long id = rs.getLong("user_id");
        String login = rs.getString("login");
        String name = rs.getString("name");
        String email = rs.getString("email");
        // Получаем дату и конвертируем её из sql.Date в time.LocalDate
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return new User(id, email, login, name, birthday);
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        Long eventId = rs.getLong("event_id");
        Long eventTimeStamp = rs.getLong("eventtimestamp");
        Long userId = rs.getLong("user_id");
        ObjectType eventType = ObjectType.valueOf(rs.getString("eventtype"));
        ActionType operation = ActionType.valueOf(rs.getString("operation"));
        Long entityId = rs.getLong("entity_id");

        return new Event(eventId, eventTimeStamp, userId, eventType, operation,entityId);
    }
}