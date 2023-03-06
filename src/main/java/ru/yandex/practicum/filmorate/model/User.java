package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class User {
    private Long Id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    private LocalDate birthday;
    @JsonIgnore
    private final List<User> friends = new ArrayList<>();

    public void addFriend(User friend) {
        friends.add(friend);
    }

    public void deleteFriend(User friend) {
        friends.remove(friend);
    }

    /*Без этого метода не проходит проверка в Postman и GitHub.
    Проверка Empty Common friends to user id=1 with user id=2 выдает ошибку Internal Server Error в InMemoryUserStorage.checkUserContains
    в месте   if (!users.containsKey(id)) {
    Я часа 2 убил на эту ошибку, уже не помню как догадался починить проблему переопределением equals
    */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.Id == user.getId();
    }
}