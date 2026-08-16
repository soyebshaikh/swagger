package com.bank.mapper;

import com.bank.model.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserMapper {
    User findById(@Param("id") Long id);
    User findByUsername(@Param("username") String username);
    List<User> findAll();
    int insert(User user);
    int update(User user);
    int deleteById(@Param("id") Long id);
}
