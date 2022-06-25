package br.com.muttley.security.server.service;

import io.jsonwebtoken.SigningKeyResolver;

/**
 * @author Joel Rodrigues Moreira on 19/02/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface SecretService {
    SigningKeyResolver getSigningKeyResolver();

    byte[] getHS256SecretBytes();

    byte[] getHS384SecretBytes();

    byte[] getHS512SecretBytes();
}
