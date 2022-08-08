package br.com.muttley.security.server.service;

import br.com.muttley.model.security.Password;
import br.com.muttley.model.security.User;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;

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

    String generateToken(final UserDetails userDetails, final Device device);

    String generateToken(final UserDetails userDetails, final Device device, Map<String, Object> details);

    String generateToken(User user, Device device);

    String generateToken(User user, Device device, Map<String, Object> details);

    boolean canTokenBeRefreshed(String token, Date lastPasswordReset);

    String refreshToken(String token);

    boolean validateTokenWithUser(String token, UserDetails userDetails);

    boolean validateTokenWithUser(String token, User user, final Password password);

    /**
     * Verifica se o token foi assinado pelo servidor e se o mesmo ainda não esta expirado
     */
    boolean isValidToken(final String token);

}
