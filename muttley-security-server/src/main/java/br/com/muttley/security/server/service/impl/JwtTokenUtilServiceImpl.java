package br.com.muttley.security.server.service.impl;

import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.Password;
import br.com.muttley.model.security.User;
import br.com.muttley.security.server.service.SecretService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class JwtTokenUtilServiceImpl implements Serializable, br.com.muttley.security.server.service.JwtTokenUtilService {

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_AUDIENCE = "audience";
    static final String CLAIM_KEY_CREATED = "created";

    private static final String AUDIENCE_UNKNOWN = "unknown";
    private static final String AUDIENCE_WEB = "web";
    private static final String AUDIENCE_MOBILE = "mobile";
    private static final String AUDIENCE_TABLET = "tablet";

    @Value("${muttley.security.jwt.token.expiration}")
    private long expiration;
    private final SecretService secretService;

    @Autowired
    public JwtTokenUtilServiceImpl(final SecretService secretService) {
        this.secretService = secretService;
        System.out.println("#chaves => "+ this.secretService.getHS512SecretBytes());
    }

    @Override
    public final String getUsernameFromToken(final String token) {
        try {
            final Claims claims = getClaimsFromToken(token);
            if (claims != null) {
                return claims.getSubject();
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public final Date getCreatedDateFromToken(final String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    @Override
    public final Date getExpirationDateFromToken(final String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    @Override
    public final String getAudienceFromToken(final String token) {
        String audience;
        try {
            final Claims claims = getClaimsFromToken(token);
            audience = (String) claims.get(CLAIM_KEY_AUDIENCE);
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    private final Claims getClaimsFromToken(final String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secretService.getHS512SecretBytes())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private final Date generateExpirationDate() {
        return Date.from(Instant.now().plusMillis(expiration));
    }

    private final boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        if (expiration != null) {
            return expiration.before(new Date());
        }
        return true;
    }

    private boolean isCreatedBeforeLastPasswordReset(final Date created, final Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private String generateAudience(final Device device) {
        String audience = AUDIENCE_UNKNOWN;
        if (device.isNormal()) {
            audience = AUDIENCE_WEB;
        } else if (device.isTablet()) {
            audience = AUDIENCE_TABLET;
        } else if (device.isMobile()) {
            audience = AUDIENCE_MOBILE;
        }
        return audience;
    }

    private final boolean ignoreTokenExpiration(final String token) {
        final String audience = getAudienceFromToken(token);
        return (AUDIENCE_TABLET.equals(audience) || AUDIENCE_MOBILE.equals(audience));
    }

    @Override
    public final String generateToken(final UserDetails userDetails, Device device) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_AUDIENCE, generateAudience(device));
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    @Override
    public String generateToken(final User user, final Device device) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, user.getUserName());
        claims.put(CLAIM_KEY_AUDIENCE, generateAudience(device));
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    final String generateToken(final Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secretService.getHS512SecretBytes())
                .compact();
    }

    @Override
    public final boolean canTokenBeRefreshed(final String token, final Date lastPasswordReset) {
        final Date created = getCreatedDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    @Override
    public final String refreshToken(final String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    @Override
    public final boolean validateTokenWithUser(final String token, final UserDetails userDetails) {
        final JwtUser user = (JwtUser) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getCreatedDateFromToken(token);
        //final Date expiration = getExpirationDateFromToken(token);
        return (
                username.equals(user.getUsername())
                        && !isTokenExpired(token)
                        && !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()));
    }

    @Override
    public boolean validateTokenWithUser(final String token, final User user, final Password password) {
        final String username = getUsernameFromToken(token);
        final Date created = getCreatedDateFromToken(token);
        //final Date expiration = getExpirationDateFromToken(token);
        return (
                username.equals(user.getUserName())
                        && !isTokenExpired(token)
                        && !isCreatedBeforeLastPasswordReset(created, password.getHistoric().getDtChange()));
    }

    @Override
    public boolean isValidToken(final String token) {
        return !StringUtils.isEmpty(getUsernameFromToken(token)) && !this.isTokenExpired(token);
    }
}
