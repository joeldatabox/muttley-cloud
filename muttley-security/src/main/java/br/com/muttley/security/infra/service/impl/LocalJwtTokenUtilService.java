package br.com.muttley.security.infra.service.impl;

import br.com.muttley.redis.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author Joel Rodrigues Moreira 24/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
class LocalJwtTokenUtilService {
    private final LocalSecretService secretService;

    LocalJwtTokenUtilService(final RedisService redisService) {
        this.secretService = new LocalSecretService(redisService);
    }

    private Claims getClaims(final boolean retry, final String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretService.getHS512SecretBytes())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {
            if (retry) {
                //se deu pau é sinal que as chaves são inválidas.
                //vamos tentar atualizar as mesmas e tentar novamente
                this.secretService.refreshSecrets();
                return this.getClaims(false, token);
            }
            throw ex;
        }
    }

    private Claims getClaims(final String token) {
        return this.getClaims(true, token);
    }

    public boolean isValidToken(final String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        //fazendo parser do token
        final Claims claims;
        try {
            claims = this.getClaims(token);

            return !this.isTokenExpired(claims) && this.existsUserName(claims);
        } catch (Exception e) {
            //se deu exception, logo o token é inválido
            return false;
        }
    }

    public Date getExpiration(final String token) {
        return this.getExpiration(this.getClaims(token));
    }

    private Date getExpiration(final Claims claims) {
        return claims.getExpiration();
    }


    private final boolean existsUserName(final Claims claims) {
        return !StringUtils.isEmpty(claims.getSubject());
    }

    private final boolean isTokenExpired(final Claims claims) {
        final Date expiration = this.getExpiration(claims);
        if (expiration != null) {
            return expiration.before(new Date());
        }
        return true;
    }
}
