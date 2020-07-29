package br.com.muttley.security.server.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.lang.Assert;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.SignatureAlgorithm.HS384;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.impl.TextCodec.BASE64;
import static io.jsonwebtoken.impl.crypto.MacProvider.generateKey;

@Service
public class SecretServiceImpl implements br.com.muttley.security.server.service.SecretService {

    private Map<String, String> secrets;
    private final SigningKeyResolver signingKeyResolver;

    @PostConstruct
    public void setup() {
        refreshSecrets();
    }

    public SecretServiceImpl() {
        this.secrets = new HashMap();
        this.signingKeyResolver = new SigningKeyResolverAdapter() {
            @Override
            public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
                return BASE64.decode(secrets.get(header.getAlgorithm()));
            }
        };
    }

    @Override
    public SigningKeyResolver getSigningKeyResolver() {
        return signingKeyResolver;
    }

    @Override
    public Map<String, String> getSecrets() {
        return secrets;
    }

    @Override
    public void setSecrets(Map<String, String> secrets) {
        Assert.notNull(secrets);
        Assert.hasText(secrets.get(HS256.getValue()));
        Assert.hasText(secrets.get(HS384.getValue()));
        Assert.hasText(secrets.get(HS512.getValue()));

        this.secrets = secrets;
    }

    @Override
    public byte[] getHS256SecretBytes() {
        return BASE64.decode(secrets.get(HS256.getValue()));
    }

    @Override
    public byte[] getHS384SecretBytes() {
        return BASE64.decode(secrets.get(HS384.getValue()));
    }

    @Override
    public byte[] getHS512SecretBytes() {
        return BASE64.decode(secrets.get(HS512.getValue()));
    }


    public final Map<String, String> refreshSecrets() {
        secrets.put(HS256.getValue(), BASE64.encode(generateKey(HS256).getEncoded()));
        secrets.put(HS384.getValue(), BASE64.encode(generateKey(HS384).getEncoded()));
        secrets.put(HS512.getValue(), BASE64.encode(generateKey(HS512).getEncoded()));
        return secrets;
    }
}
