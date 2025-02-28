package io.test.service;

import io.test.extension.GlobalExtension;
import io.test.paramresolver.UserServiceTestParamResolver;
import org.example.model.User;
import org.example.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
@ExtendWith({
        UserServiceTestParamResolver.class,
        GlobalExtension.class
})
class UserServiceTest {

    private static final User EONE = User.of(1, "e-one", "pass");
    private static final User PETER = User.of(2, "peter", "pass");

    private UserService userService;

    public UserServiceTest(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @BeforeEach
    void prepare(UserService userService) {
        this.userService = userService;
    }

    @AfterEach
    void cleanup() {
        System.out.println("After each: " + this);
    }

    @DisplayName("Users will be empty if no users added")
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

    @Test
    void checkLoginPerformance() {
        var result = assertTimeout(
                Duration.ofMillis(1L), () -> {
                    Thread.sleep(2L);
                    return userService.login(EONE.getUsername(), EONE.getPassword());
                }
        );
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForLoginTest")
    void loginParametrizedTest(String username, String password, Optional<User> user) {
        userService.add(EONE, PETER);

        var userOpt = userService.login(username, password);
        assertThat(userOpt).isEqualTo(user);
    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("e-one", "pass", Optional.of(EONE)),
                Arguments.of("asdas", "asda", Optional.of(PETER)),
                Arguments.of("dummy", "dummy", Optional.empty())
        );
    }
}
