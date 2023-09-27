package br.com.muttley.localcache.services;

public interface LocalRSAKeyPairService {
    public static final String BASIC_KEY_PRIVATE = "RSA:PRIVATE";
    public static final String BASIC_KEY_PUBLIC = "RSA:PUBLIC";

    String encryptMessage(final String message);

    String decryptMessage(final String encryptedMessage);
}
