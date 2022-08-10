package br.com.muttley.security.server.components;

import br.com.muttley.model.security.rsa.RSAUtil;

import java.security.Key;
import java.security.PrivateKey;

/**
 * @author Joel Rodrigues Moreira on 10/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class RSAPairKeyComponent {


    private static class LocalKeyPair {
        private static PrivateKey privateKey;

        public static final Key getKey(final String location) {
            if (privateKey == null) {
                privateKey = RSAUtil.readPrivateKeyFromFile(location);
            }

        }

    }
}
