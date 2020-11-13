package br.com.muttley.report;

/**
 * @author Joel Rodrigues Moreira 10/11/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyReportBuilder<T extends MuttleyReportBuilder> {

    MuttleyReportBuilder addParam(final String key, final Object value);

    MuttleyReport build();
}
