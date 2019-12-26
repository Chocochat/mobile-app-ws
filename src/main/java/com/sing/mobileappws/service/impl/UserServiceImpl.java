package com.sing.mobileappws.service.impl;

import com.sing.mobileappws.exceptions.UserServiceExceptions;
import com.sing.mobileappws.io.entity.UserEntity;
import com.sing.mobileappws.io.repositories.UserRepository;
import com.sing.mobileappws.service.AddressDTO;
import com.sing.mobileappws.service.UserDto;
import com.sing.mobileappws.service.UserService;
import com.sing.mobileappws.ui.model.response.ErrorMessages;
import com.sing.mobileappws.ui.shared.AmazonSES;
import com.sing.mobileappws.ui.shared.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        if (userRepository.findByEmail(userDto.getEmail()) != null)
            throw new RuntimeException("Record already exists: " + userDto.getEmail());

        for (int i = 0; i < userDto.getAddresses().size(); i++) {
            AddressDTO address = userDto.getAddresses().get(i);
            address.setUserDetails(userDto);
            address.setAddressId(utils.generateAddressId(30));
            userDto.getAddresses().set(i, address);
        }

//        UserEntity userEntity = new UserEntity();
//        BeanUtils.copyProperties(userDto, userEntity);

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setUserId(publicUserId);
        // generate and set token
        userEntity.setEmailVerificationToken(utils.generateEmailVerifivationToken(publicUserId));
        userEntity.setEmailVerificationStatus(false);

        UserEntity save = userRepository.save(userEntity);

//        UserDto response = new UserDto();
//        BeanUtils.copyProperties(save, response);

        UserDto response = modelMapper.map(save, UserDto.class);

        //send Email
        new AmazonSES().verifyEmail(response);

        return response;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

//        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());

        return new User(
                userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                userEntity.getEmailVerificationStatus(),
                true,
                true,
                true,
                new ArrayList<>());
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public UserDto getUserByUserID(String id) {
        UserDto userDto = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) throw new UserServiceExceptions("User with Id " + id + " not found");
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        UserDto response = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);

        if (userEntity == null) throw new UserServiceExceptions(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());

        UserEntity entity = userRepository.save(userEntity);

        BeanUtils.copyProperties(entity, response);

        return response;
    }

    @Override
    public void deleteUser(String id) {
        UserEntity userEntity = userRepository.findByUserId(id);

        if (userEntity == null) throw new UserServiceExceptions(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int count) {

        List<UserDto> userList = new ArrayList<>();

        if (page > 0) page = page - 1;

        Pageable pageableRequest = PageRequest.of(page, count);

        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto response = new UserDto();
            BeanUtils.copyProperties(userEntity, response);
            userList.add(response);
        }

        return userList;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean emailVerified = false;

        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if (userEntity != null) {
            boolean hasTokenExpired = utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                emailVerified = true;
            }
        }

        return emailVerified;
    }

}
