package br.com.muttley.security.infra.service.impl;

import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.redis.service.RedisService;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.SignatureAlgorithm.HS384;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.impl.TextCodec.BASE64;


/**
 * @author Joel Rodrigues Moreira 24/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Classe responsável por prover acesso as chaves de assinatura dos tokens gerados pelo servidor de segurança
 */
class LocalSecretService {
    private static final String BASIC_KEY = "BASIC_SERVER_KEY";
    private Map<String, String> secrets;
    //private final RedisService redisService;

    public LocalSecretService(final RedisService redisService) {
        this.secrets = new HashMap(3);
        this.refreshSecrets(redisService);
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


    private final void refreshSecrets(final RedisService redisService) {
        try {
            //secrets.put(HS256.getValue(), BASE64.encode(generateKey(HS256).getEncoded()));
            this.addSecret(redisService, HS256.getValue());
            //secrets.put(HS384.getValue(), BASE64.encode(generateKey(HS384).getEncoded()));
            this.addSecret(redisService, HS384.getValue());
            //secrets.put(HS512.getValue(), BASE64.encode(generateKey(HS512).getEncoded()));
            this.addSecret(redisService, HS512.getValue());
        } catch (Exception exception) {
            LoggerFactory.getLogger(LocalSecretService.class).error("ATENÇÃO! NÃO FOI ENCONTRADO CHAVE GERADAS PELO SERVIDOR DE SERGURANÇA");
            exception.printStackTrace();
        }
        //return secrets;
    }

    private final void addSecret(final RedisService redisService, final String key) {
        if (redisService.hasKey(this.getBasicKey(key))) {
            this.secrets.put(key, (String) redisService.get(this.getBasicKey(key)));
        } else {
            throw new MuttleyException("Não foi encontrado as chaves necessárias no sistema");
        }
    }

    private String getBasicKey(final String key) {
        return BASIC_KEY + ":" + key;
    }
}
