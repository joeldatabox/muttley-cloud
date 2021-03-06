package br.com.muttley.security.server.service;

import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 28/07/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface JwtTokenUtilService {
    String getUsernameFromToken(String token);

    Date getCreatedDateFromToken(String token);

    Date getExpirationDateFromToken(String token);

    String getAudienceFromToken(String token);

    String generateToken(UserDetails userDetails, Device device);

    boolean canTokenBeRefreshed(String token, Date lastPasswordReset);

    String refreshToken(String token);

    boolean validateToken(String token, UserDetails userDetails);
}
