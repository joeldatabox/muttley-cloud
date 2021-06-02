package br.com.muttley.headers.components;

/**
 * @author Joel Rodrigues Moreira on 02/06/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyCurrentVersion {
    String getCurrentValue();

    boolean containsValidValue();

    String getCurrenteFromServer();
}
