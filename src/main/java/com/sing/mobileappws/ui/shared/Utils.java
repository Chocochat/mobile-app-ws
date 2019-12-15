package com.sing.mobileappws.ui.shared;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class Utils {
    public String generateUserId(int length){
        String generatedString = RandomStringUtils.randomAlphanumeric(length);
        return generatedString;
    }

}
