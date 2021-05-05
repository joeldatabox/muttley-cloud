package br.com.muttley.headers.components;

import br.com.muttley.headers.model.MuttleyHeader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 29/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component("currentVersion")
@RequestScope
public class MuttleyCurrentVersion extends MuttleyHeader {
    public static final String CURRENT_VERION = "Current-Version";
    private final BuildProperties buildProperties;

    @Autowired
    public MuttleyCurrentVersion(final ObjectProvider<HttpServletRequest> requestProvider, final BuildProperties buildProperties) {
        super(CURRENT_VERION, requestProvider);
        this.buildProperties = buildProperties;
    }

    @Override
    public String getCurrentValue() {
        if (this.containsValidValue()) {
            return super.getCurrentValue();
        }
        return null;
    }

    @Override
    public boolean containsValidValue() {
        return !isEmpty(this.currentValue);
    }


    public String getCurrenteFromServer() {
        return this.buildProperties.getVersion();
    }
}
