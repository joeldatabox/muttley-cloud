package br.com.muttley.localcache.services.impl;

import br.com.muttley.localcache.services.LocalRSAKeyPairService;
import br.com.muttley.model.security.rsa.RSAUtil;
import br.com.muttley.redis.service.RedisService;

import java.security.PrivateKey;
import java.security.PublicKey;

import static br.com.muttley.model.security.rsa.RSAUtil.decrypt;
import static br.com.muttley.model.security.rsa.RSAUtil.encrypt;
import static br.com.muttley.model.security.rsa.RSAUtil.readPrivateKeyFromString;
import static br.com.muttley.model.security.rsa.RSAUtil.readPublicKeyFromString;

public abstract class AbstractLocalRSAKeyPairService implements LocalRSAKeyPairService {
    protected PrivateKey privateKey;
    protected PublicKey publicKey;
    protected final RedisService service;

    protected AbstractLocalRSAKeyPairService(RedisService service) {
        this.service = service;
    }

    protected String getBasicKeyPublic() {
        return BASIC_KEY_PUBLIC;
    }

    protected String getBasicKeyPrivate() {
        return BASIC_KEY_PRIVATE;
    }

    @Override
    public String encryptMessage(String message) {
        return encrypt(getPrivateKey(), message);
    }

    @Override
    public String decryptMessage(String encryptedMessage) {
        return decrypt(getPublicKey(), encryptedMessage);
    }

    protected PrivateKey getPrivateKey() {
        if (privateKey == null) {
            privateKey = readPrivateKeyFromString((String) this.service.get(this.getBasicKeyPrivate()));
        }
        return privateKey;
    }

    protected void setPrivateKey(final PrivateKey key) {
        this.privateKey = key;
        this.service.set(this.getBasicKeyPrivate(), RSAUtil.toString(key));
    }

    protected PublicKey getPublicKey() {
        if (publicKey == null) {
            publicKey = readPublicKeyFromString((String) this.service.get(this.getBasicKeyPublic()));
        }
        return publicKey;
    }

    protected void setPublicKey(final PublicKey key) {
        this.publicKey = key;
        this.service.set(this.getBasicKeyPublic(), RSAUtil.toString(key));
    }
}
