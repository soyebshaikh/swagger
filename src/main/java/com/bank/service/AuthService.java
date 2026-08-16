package com.bank.service;

import com.bank.model.User;

public interface AuthService {
    User login(String username, String password);
    User register(User user);
    User getUserById(Long id);
}
