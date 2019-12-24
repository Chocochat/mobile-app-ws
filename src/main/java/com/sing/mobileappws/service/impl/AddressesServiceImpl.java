package com.sing.mobileappws.service.impl;

import com.sing.mobileappws.exceptions.UserServiceExceptions;
import com.sing.mobileappws.io.entity.AddressEntity;
import com.sing.mobileappws.io.entity.UserEntity;
import com.sing.mobileappws.io.repositories.AddressRepository;
import com.sing.mobileappws.io.repositories.UserRepository;
import com.sing.mobileappws.service.AddressDTO;
import com.sing.mobileappws.service.AddressesService;
import com.sing.mobileappws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressesServiceImpl implements AddressesService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDTO> getAddresses(String userId) {
        List<AddressDTO> resp = new ArrayList<>();

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) throw new UserServiceExceptions(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);

        for (AddressEntity addressEntity: addresses){

            resp.add(new ModelMapper().map(addressEntity, AddressDTO.class));
        }

        return resp;
    }

    @Override
    public AddressDTO getAddress(String addressId) {
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
        return new ModelMapper().map(addressEntity, AddressDTO.class);
    }
}
