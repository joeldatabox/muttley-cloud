package br.com.muttley.mongo.config.source;

/**
 * @author Joel Rodrigues Moreira on 18/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface ViewRepository {
    AbstractView findByName(final String name);

    AbstractView save(AbstractView view);
}
