package pl.cinema.service;

import pl.cinema.model.User;

public interface UserService {
    public User findUserByEmail(String email);
    public void saveUser(User user);
}