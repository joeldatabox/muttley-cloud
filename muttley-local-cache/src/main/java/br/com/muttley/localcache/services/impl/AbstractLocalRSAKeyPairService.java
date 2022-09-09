package br.com.muttley.localcache.services.impl;

import br.com.muttley.localcache.services.LocalRSAKeyPairService;
import br.com.muttley.model.security.rsa.RSAUtil;
import br.com.muttley.redis.service.RedisService;

import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class AbstractLocalRSAKeyPairService implements LocalRSAKeyPairService {
    protected PrivateKey privateKey;
    protected PublicKey publicKey;
    protected final RedisService service;

    protected AbstractLocalRSAKeyPairService(RedisService service) {
        this.service = service;
    }

    protected String getBasicKeyPublic() {
        return LocalRSAKeyPairService.BASIC_KEY_PUBLIC;
    }

    protected String getBasicKeyPrivate() {
        return LocalRSAKeyPairService.BASIC_KEY_PRIVATE;
    }

    @Override
    public String encryptMessage(String message) {
        return null;
    }

    @Override
    public String decryptMessage(String encryptedMessage) {
        return null;
    }

    protected PrivateKey getPrivateKey() {
        if (privateKey == null) {
            privateKey = RSAUtil.readPrivateKeyFromString((String) this.service.get(this.getBasicKeyPrivate()));
        }
        return privateKey;
    }

    protected void setPrivateKey(final PrivateKey key) {
        this.privateKey = key;
        this.service.set(this.getBasicKeyPrivate(), RSAUtil.toString(key));
    }

    protected PublicKey getPublicKey() {
        if (publicKey == null) {
            publicKey = RSAUtil.readPublicKeyFromString((String) this.service.get(this.getBasicKeyPublic()));
        }
        return publicKey;
    }

    protected void setPublicKey(final PublicKey key) {
        this.publicKey = key;
        this.service.set(this.getBasicKeyPublic(), RSAUtil.toString(key));
    }
}
