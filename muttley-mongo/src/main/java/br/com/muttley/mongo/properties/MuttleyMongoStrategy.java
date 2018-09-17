package br.com.muttley.mongo.properties;

/**
 * @author Joel Rodrigues Moreira on 26/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
public enum MuttleyMongoStrategy {
    MultiTenancy,
    SimpleTenancy;

    public boolean isMultiTenancyDocument() {
        return this.equals(MultiTenancy);
    }

    public boolean isSimpleTenancyDocument() {
        return this.equals(SimpleTenancy);
    }

}
