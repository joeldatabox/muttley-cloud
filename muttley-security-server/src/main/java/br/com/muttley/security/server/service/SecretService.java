package br.com.muttley.security.server.service;

import io.jsonwebtoken.SigningKeyResolver;

import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 28/07/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface SecretService {
    SigningKeyResolver getSigningKeyResolver();

    Map<String, String> getSecrets();

    void setSecrets(Map<String, String> secrets);

    byte[] getHS256SecretBytes();

    byte[] getHS384SecretBytes();

    byte[] getHS512SecretBytes();
}
