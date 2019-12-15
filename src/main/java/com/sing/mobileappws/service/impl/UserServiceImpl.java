package com.sing.mobileappws.service.impl;

import com.sing.mobileappws.UserRepository;
import com.sing.mobileappws.io.entity.UserEntity;
import com.sing.mobileappws.service.UserDto;
import com.sing.mobileappws.service.UserService;
import com.sing.mobileappws.ui.shared.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {

        if(userRepository.findByEmail(userDto.getEmail())!= null) throw new RuntimeException("Record already exists: "+ userDto.getEmail());

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userDto, userEntity);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setUserId(utils.generateUserId(30));

        UserEntity save = userRepository.save(userEntity);

        UserDto response = new UserDto();
        BeanUtils.copyProperties(save, response);

        return response;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
