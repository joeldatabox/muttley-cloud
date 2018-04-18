package br.com.muttley.security.server.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.impl.crypto.MacProvider;
import io.jsonwebtoken.lang.Assert;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.SignatureAlgorithm.HS384;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.impl.TextCodec.BASE64;

@Service
public class SecretService {

    private Map<String, String> secrets;
    private final SigningKeyResolver signingKeyResolver;

    @PostConstruct
    public void setup() {
        refreshSecrets();
    }

    public SecretService() {
        this.secrets = new HashMap();
        this.signingKeyResolver = new SigningKeyResolverAdapter() {
            @Override
            public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
                return BASE64.decode(secrets.get(header.getAlgorithm()));
            }
        };
    }

    public SigningKeyResolver getSigningKeyResolver() {
        return signingKeyResolver;
    }

    public Map<String, String> getSecrets() {
        return secrets;
    }

    public void setSecrets(Map<String, String> secrets) {
        Assert.notNull(secrets);
        Assert.hasText(secrets.get(HS256.getValue()));
        Assert.hasText(secrets.get(HS384.getValue()));
        Assert.hasText(secrets.get(HS512.getValue()));

        this.secrets = secrets;
    }

    public byte[] getHS256SecretBytes() {
        return BASE64.decode(secrets.get(HS256.getValue()));
    }

    public byte[] getHS384SecretBytes() {
        return BASE64.decode(secrets.get(HS384.getValue()));
    }

    public byte[] getHS512SecretBytes() {
        return BASE64.decode(secrets.get(HS512.getValue()));
    }


    public final Map<String, String> refreshSecrets() {
        SecretKey key = MacProvider.generateKey(HS256);
        secrets.put(HS256.getValue(), BASE64.encode(key.getEncoded()));
        key = MacProvider.generateKey(HS384);
        secrets.put(HS384.getValue(), BASE64.encode(key.getEncoded()));
        key = MacProvider.generateKey(HS512);
        secrets.put(HS512.getValue(), BASE64.encode(key.getEncoded()));
        return secrets;
    }
}