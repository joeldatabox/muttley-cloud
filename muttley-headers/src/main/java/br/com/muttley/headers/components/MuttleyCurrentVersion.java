package br.com.muttley.headers.components;

import br.com.muttley.headers.model.MuttleyHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 13/05/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component("currentVersion")
@RequestScope
public class MuttleyCurrentVersion extends MuttleyHeader {
    public static final String CURRENT_VERION = "Current-Version";
    private final BuildProperties buildProperties;

    @Autowired
    public MuttleyCurrentVersion(final HttpServletRequest request, final BuildProperties buildProperties) {
        super(CURRENT_VERION, request);
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
