package com.sing.mobileappws.ui.model.response;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class AddressesRest extends RepresentationModel {

    private String addressId;
    private String city;
    private String country;
    private String streetName;
    private String postalCode;
    private String type;
}
