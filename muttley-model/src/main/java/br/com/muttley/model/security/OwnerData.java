package br.com.muttley.model.security;

/**
 * @author Joel Rodrigues Moreira 29/12/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Interface criada para Evitar o vazamente do informações  a respeito do owner para terceiros
 */
public interface OwnerData {
    String getId();

    String getName();

    String getDescription();

    UserData getUserMaster();
}
