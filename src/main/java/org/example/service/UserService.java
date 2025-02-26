package org.example.service;

import org.example.model.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

public class UserService {
    
    private final List<User> users = new ArrayList<>();
    
    public List<User> getAll() {
        return users;
    }

    public void add(User... us) {
        users.addAll(Arrays.asList(us));
    }

    public Optional<User> login(String username, String password) {
        if (Objects.isNull(username) || Objects.isNull(password)) {
            throw new IllegalArgumentException("Username and password are required");
        }
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return users.stream().collect(toMap(User::getId, identity()));
    }
}
