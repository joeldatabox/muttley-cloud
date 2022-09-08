package br.com.muttley.security.server.components;

import br.com.muttley.model.security.rsa.RSAUtil;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Joel Rodrigues Moreira on 10/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class RSAPairKeyComponent {


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
