package com.sing.mobileappws.ui.controller;

import com.sing.mobileappws.service.UserDto;
import com.sing.mobileappws.service.UserService;
import com.sing.mobileappws.ui.model.request.UserDetailsRequestModel;
import com.sing.mobileappws.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping(
            path = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public UserRest getUser(@PathVariable String id) {
        UserRest response = new UserRest();

        UserDto userDto = userService.getUserByUserID(id);

        BeanUtils.copyProperties(userDto, response);
        return response;
    }

    @PostMapping(
            consumes = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            })
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetailsRequestModel) {
        UserRest response = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetailsRequestModel, userDto);

        UserDto createUser = userService.createUser(userDto);

        BeanUtils.copyProperties(createUser, response);

        return response;
    }

    @PutMapping
    public String updateUser() {
        return "put";
    }

    @DeleteMapping
    public String deleteUser() {
        return "Delete";
    }
}
