package br.com.muttley.report;

import br.com.muttley.headers.components.MuttleyCurrentTimezone;
import br.com.muttley.headers.components.MuttleyCurrentVersion;
import br.com.muttley.model.security.User;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;

/**
 * @author Joel Rodrigues Moreira 10/11/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyReportBuilder<T extends MuttleyReportBuilder> {

    MongoTemplate getTemplate();

    User getCurrentUser();

    MuttleyCurrentVersion getCurrentVersion();

    MuttleyCurrentTimezone getCurrentTimezone();

    T addParam(final String key, final Object value);

    T addSubReport(final MuttleyReportBuilder report);

    Map<String, Object> getParams();

    MuttleyReport build();

    Map<String, Object> getParamsForSubReport();
}
