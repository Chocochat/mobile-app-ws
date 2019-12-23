package com.sing.mobileappws.ui.controller;

import com.sing.mobileappws.exceptions.UserServiceExceptions;
import com.sing.mobileappws.service.UserDto;
import com.sing.mobileappws.service.UserService;
import com.sing.mobileappws.ui.model.request.RequestOperationName;
import com.sing.mobileappws.ui.model.request.RequestOperationStatus;
import com.sing.mobileappws.ui.model.request.UserDetailsRequestModel;
import com.sing.mobileappws.ui.model.response.ErrorMessages;
import com.sing.mobileappws.ui.model.response.OperationStatusModel;
import com.sing.mobileappws.ui.model.response.UserRest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetailsRequestModel) {

        if (StringUtils.isAllEmpty(userDetailsRequestModel.getFirstName())) {
            throw new UserServiceExceptions(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
//            Example to trigger handleOtherException
//            throw new NullPointerException("null");
        }

        UserRest response = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetailsRequestModel, userDto);

        UserDto createUser = userService.createUser(userDto);

        BeanUtils.copyProperties(createUser, response);

        return response;
    }

    @PutMapping(
            path = "/{id}",
            consumes = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetailsRequestModel) {

        if (StringUtils.isAllEmpty(userDetailsRequestModel.getFirstName())) {
            throw new UserServiceExceptions(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
//            Example to trigger handleOtherException
//            throw new NullPointerException("null");
        }

        UserRest response = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetailsRequestModel, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);

        BeanUtils.copyProperties(updatedUser, response);

        return response;
    }

    @DeleteMapping(
            path = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public OperationStatusModel deleteUser(@PathVariable String id) {

        OperationStatusModel operationStatusModel = new OperationStatusModel();

        userService.deleteUser(id);

        operationStatusModel.setOperationName(RequestOperationName.DELETE.name());
        operationStatusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return operationStatusModel;
    }

    @GetMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {

        List<UserRest> response = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        for (UserDto user : users){
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(user, userRest);
            response.add(userRest);
        }

        return response;

    }
}
