package io.test.service;

import org.example.model.User;
import org.example.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class UserServiceTest {

    private static final User EONE = User.of(1, "e-one", "pass");
    private static final User PETER = User.of(2, "peter", "pass");

    private UserService userService;

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
        userService = new UserService();
    }

    @AfterEach
    void cleanup() {
        System.out.println("After each: " + this);
    }

    @Test
    void usersEmptyIfNoUsersAdded() {
        var users = userService.getAll();
        assertFalse(!users.isEmpty(), () -> "There are no users added");
    }

    @Test
    void usersSizeIfUserAdded() {
        userService.add(EONE);
        userService.add(PETER);

        var users = userService.getAll();

        assertThat(users).hasSize(2);
    }

    @Test
    @Tag("test") @Tag("tag")
    void loginFailIfPasswordIncorrect() {
        userService.add(EONE);

        var userOptional = userService.login(EONE.getUsername(), "dummy");

        assertThat(userOptional).isNotPresent();
    }

    @Test
    void loginSuccessIfUserExists() {
        userService.add(EONE);
        Optional<User> userOptional = userService.login(EONE.getUsername(), EONE.getPassword());
        assertThat(userOptional).isPresent();
        userOptional.ifPresent(user -> assertThat(EONE).isEqualTo(user));
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(EONE, PETER);
        Map<Integer, User> usersMap = userService.getAllConvertedById();

        assertAll(
                () -> assertThat(usersMap).containsKeys(EONE.getId(), PETER.getId()),
                () -> assertThat(usersMap).containsValues(EONE, PETER)
        );
    }

    @Test
    void throwExceptionIfUserOrPasswordIsNull() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
        );
    }
}
