package com.sing.mobileappws.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);

    UserDto getUser(String email);

    UserDto getUserByUserID(String id);

    UserDto updateUser(String id, UserDto userDto);

    void deleteUser(String id);

    List<UserDto> getUsers(int page, int count);

    boolean verifyEmailToken(String token);

    boolean requestPasswordReset(String email);
}
