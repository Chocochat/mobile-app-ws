package com.sing.mobileappws.ui.shared;

import com.sing.mobileappws.security.SecutityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.sing.mobileappws.security.SecutityConstants.EXPIRATION_TIME;


@Component
public class Utils {
    public String generateUserId(int length) {
        String generatedString = RandomStringUtils.randomAlphanumeric(length);
        return generatedString;
    }

    public String generateAddressId(int length) {
        String generatedString = RandomStringUtils.randomAlphanumeric(length);
        return generatedString;
    }

    public boolean hasTokenExpired(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecutityConstants.getTokenSecret())
                .parseClaimsJws(token).getBody();
        Date tokenExpirationDate = claims.getExpiration();
        Date today = new Date();

        return tokenExpirationDate.before(today);
    }

    public String generateEmailVerifivationToken(String publicUserId) {
        return Jwts.builder()
                .setSubject(publicUserId)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecutityConstants.getTokenSecret())
                .compact();
    }
}
