package br.com.muttley.security.server.components;

import br.com.muttley.localcache.services.LocalRSAKeyPairService;
import br.com.muttley.model.security.rsa.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static br.com.muttley.model.security.rsa.RSAUtil.generateRandomString;

/**
 * @author Joel Rodrigues Moreira on 10/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@EnableConfigurationProperties(RSAPairKeyProperty.class)
@Component
public class RSAPairKeyComponent implements LocalRSAKeyPairService {
    private final RSAPairKeyProperty properties;


    @Autowired
    public RSAPairKeyComponent(final RSAPairKeyProperty properties) {
        this.properties = properties;
    }

    @PostConstruct
    private void init() {
        final File privateKeyFile = new File(properties.getPrivateKeyFile());
        final File publicKeyFile = new File(properties.getPublicKeyFile());
        //verificando se precisa criar a chave
        if (properties.isAutoCriateIfNotExists() && !privateKeyFile.exists()) {
            final KeyPair keyPair = RSAUtil.createKeyPair(4096, StringUtils.isEmpty(this.properties.getSeed()) ? generateRandomString(15000) : this.properties.getSeed());
            RSAUtil.write(privateKeyFile, keyPair.getPrivate());
            RSAUtil.write(publicKeyFile, keyPair.getPublic());
        }

    }

    @Override
    public String encryptMessage(String message) {
        return RSAUtil.encrypt(LocalKeyPair.getPrivateKey(this.properties.getPrivateKeyFile()), message);
    }

    @Override
    public String decryptMessage(String encryptedMessage) {
        return RSAUtil.decrypt(LocalKeyPair.getPublicKey(this.properties.getPrivateKeyFile()), encryptedMessage);
    }


    private static class LocalKeyPair {
        private static PrivateKey privateKey;
        private static PublicKey publicKey;

        public static final Key getPrivateKey(final String location) {
            if (privateKey == null) {
                privateKey = RSAUtil.readPrivateKeyFromFile(location);
            }
            return privateKey;
        }

        public static final Key getPublicKey(final String location) {
            if (publicKey == null) {
                publicKey = RSAUtil.readPublicKeyFromFile(location);
            }
            return publicKey;
        }

    }
}
