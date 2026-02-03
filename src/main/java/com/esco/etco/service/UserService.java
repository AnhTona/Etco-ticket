package com.esco.etco.service;

import com.esco.etco.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    User createUser(User user);

    List<User> getAllUsers();

    User getUserById(long id);

    User updateUser(User updateUser);

    void deleteUserById(long id);

    boolean getUserByEmail(String email);
}
