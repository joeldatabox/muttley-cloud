package br.com.muttley.report;

/**
 * @author Joel Rodrigues Moreira on 21/07/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyReportBuilder {

    MuttleyReportBuilder addParam(final String key, final Object value);

    MuttleyReport build();
}

