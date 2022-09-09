package br.com.muttley.security.server.components;

import br.com.muttley.localcache.services.LocalRSAKeyPairService;
import br.com.muttley.localcache.services.impl.AbstractLocalRSAKeyPairService;
import br.com.muttley.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static br.com.muttley.model.security.rsa.RSAUtil.*;

/**
 * @author Joel Rodrigues Moreira on 10/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@EnableConfigurationProperties(RSAPairKeyProperty.class)
@Component
@DependsOn("clearRedis")
public class RSAPairKeyComponent extends AbstractLocalRSAKeyPairService implements LocalRSAKeyPairService, ApplicationListener<ApplicationReadyEvent> {
    private final RSAPairKeyProperty properties;


    @Autowired
    public RSAPairKeyComponent(final RedisService service, final RSAPairKeyProperty properties) {
        super(service);
        this.properties = properties;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        final File privateKeyFile = new File(properties.getPrivateKeyFile());
        final File publicKeyFile = new File(properties.getPublicKeyFile());
        //verificando se precisa criar a chave
        if (properties.isAutoCriateIfNotExists() && !privateKeyFile.exists()) {
            final KeyPair keyPair = createKeyPair(4096, StringUtils.isEmpty(this.properties.getSeed()) ? generateRandomString(15000) : this.properties.getSeed());
            write(privateKeyFile, keyPair.getPrivate());
            write(publicKeyFile, keyPair.getPublic());
        }
        //forçando o carregamento do par de chaves
        this.getPrivateKey();
        this.getPublicKey();
    }

    @Override
    public String encryptMessage(String message) {
        return encrypt(getPrivateKey(), message);
    }

    @Override
    public String decryptMessage(String encryptedMessage) {
        return decrypt(getPublicKey(), encryptedMessage);
    }

    @Override
    protected PrivateKey getPrivateKey() {
        if (this.privateKey == null) {
            this.privateKey = readPrivateKeyFromFile(this.properties.getPrivateKeyFile());
            this.setPrivateKey(privateKey);
        }
        return privateKey;
    }

    @Override
    protected PublicKey getPublicKey() {
        if (this.publicKey == null) {
            this.publicKey = readPublicKeyFromFile(this.properties.getPublicKeyFile());
            this.setPublicKey(this.publicKey);
        }
        return publicKey;
    }
}
