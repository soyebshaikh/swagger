package com.bank.service;

import com.bank.mapper.UserMapper;
import com.bank.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserMapper userMapper;

    public AuthServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User login(String username, String password) {
        logger.info("Attempting login for user: {}", username);
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        logger.info("Login successful for user: {} (ID: {})", username, user.getId());
        return user;
    }

    @Override
    public User register(User user) {
        logger.info("Registering new user: {}", user.getUsername());
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        if (user.getRole() == null) {
            user.setRole("CUSTOMER");
        }
        userMapper.insert(user);
        logger.info("User registered successfully with ID: {}", user.getId());
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }
}
