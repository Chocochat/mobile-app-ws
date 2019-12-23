package com.sing.mobileappws.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);

    UserDto getUser(String email);

    UserDto getUserByUserID(String id);

    UserDto updateUser(String id, UserDto userDto);

    void deleteUser(String id);
}
