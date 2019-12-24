package com.sing.mobileappws.service;

import java.util.List;

public interface AddressesService {
    List<AddressDTO> getAddresses(String userId);

    AddressDTO getAddress(String addressId);
}
