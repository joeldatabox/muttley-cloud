package br.com.muttley.security.server.service;

import br.com.muttley.model.security.Password;
import br.com.muttley.model.security.User;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 19/02/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface JwtTokenUtilService {
    String getUsernameFromToken(String token);

    Date getCreatedDateFromToken(String token);

    Date getExpirationDateFromToken(String token);

    String getAudienceFromToken(String token);

    String generateToken(UserDetails userDetails, Device device);

    String generateToken(User user, Device device);

    boolean canTokenBeRefreshed(String token, Date lastPasswordReset);

    String refreshToken(String token);

    boolean validateToken(String token, UserDetails userDetails);

    boolean validateToken(String token, User user, final Password password);
}
