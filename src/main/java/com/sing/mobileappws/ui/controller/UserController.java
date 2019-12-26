package com.sing.mobileappws.ui.controller;

import com.sing.mobileappws.exceptions.UserServiceExceptions;
import com.sing.mobileappws.service.AddressDTO;
import com.sing.mobileappws.service.AddressesService;
import com.sing.mobileappws.service.UserDto;
import com.sing.mobileappws.service.UserService;
import com.sing.mobileappws.ui.model.request.RequestOperationName;
import com.sing.mobileappws.ui.model.request.RequestOperationStatus;
import com.sing.mobileappws.ui.model.request.UserDetailsRequestModel;
import com.sing.mobileappws.ui.model.response.AddressesRest;
import com.sing.mobileappws.ui.model.response.ErrorMessages;
import com.sing.mobileappws.ui.model.response.OperationStatusModel;
import com.sing.mobileappws.ui.model.response.UserRest;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressesService addressesService;

    @GetMapping(
            path = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public UserRest getUser(@PathVariable String id) {

        UserDto userDto = userService.getUserByUserID(id);

//        UserRest response = new UserRest();
//        BeanUtils.copyProperties(userDto, response);
        ModelMapper modelMapper = new ModelMapper();
        UserRest response = modelMapper.map(userDto, UserRest.class);

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

//        UserRest response = new UserRest();
//        BeanUtils doesnt copy properties for nested objects so mobelmapper can be user
//        UserDto userDto = new UserDto();
//        BeanUtils.copyProperties(userDetailsRequestModel, userDto);

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetailsRequestModel, UserDto.class);

        UserDto createUser = userService.createUser(userDto);

//        BeanUtils.copyProperties(createUser, response);
        UserRest response = modelMapper.map(createUser, UserRest.class);
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

    @GetMapping(
            path = "/{id}/addresses",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    "application/hal+json"
            })
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {

        List<AddressesRest> resp = new ArrayList<>();

        List<AddressDTO> addressDTO = addressesService.getAddresses(id);

        if (addressDTO != null && !addressDTO.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            resp = new ModelMapper().map(addressDTO, listType);
            for (AddressesRest addressesRest: resp) {
                Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressesRest.getAddressId())).withSelfRel();
                addressesRest.add(addressLink);
                Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
                addressesRest.add(userLink);
            }
        }

        return new CollectionModel<>(resp);
    }

    @GetMapping(
            path = "/{id}/addresses/{addressId}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    "application/hal+json"
            })
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String id, @PathVariable String addressId) {


        AddressDTO addressDTO = addressesService.getAddress(addressId);

//        Link addressLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).slash("addresses").slash(addressId).withSelfRel();
        //using /*   methodOn   */
        Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressId)).withSelfRel();

        Link userLink = linkTo(UserController.class).slash(id).withRel("user");

//        Link addressessLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).slash("addresses").withRel("addresses");
        //using /*   methodOn   */
        Link addressessLink = linkTo(methodOn(UserController.class).getUserAddresses(id)).withRel("addresses");

        AddressesRest resp = new ModelMapper().map(addressDTO, AddressesRest.class);
        resp.add(addressLink);
        resp.add(userLink);
        resp.add(addressessLink);

        return new EntityModel<>(resp);
    }

    @GetMapping(
            path = "/email-verification",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

        OperationStatusModel response = new OperationStatusModel();

        response.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified) {
            response.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            response.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return response;
    }
}
