package br.com.muttley.headers.components;

import br.com.muttley.headers.model.MuttleyHeader;
import br.com.muttley.model.TimeZoneDocument;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import java.util.TimeZone;

import static br.com.muttley.model.TimeZoneDocument.getTimezoneFromId;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 29/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component("currentTimezone")
@RequestScope
public class MuttleyCurrentTimezone extends MuttleyHeader {
    public static final String CURRENT_TIMEZONE = "Current-Timezone";

    public MuttleyCurrentTimezone(@Autowired final ObjectProvider<HttpServletRequest> requestProvider) {
        super(CURRENT_TIMEZONE, requestProvider);
    }

    @Override
    public String getCurrentValue() {
        if (this.containsValidValue()) {
            if (this.isValid()) {
                String currentTimezone = super.getCurrentValue();

                if (!(currentTimezone.startsWith("+") || currentTimezone.startsWith("-"))) {
                    //se chegou aqui logo é timezone positivo
                    currentTimezone = "+" + currentTimezone;
                }
                if (!currentTimezone.contains(":")) {
                    currentTimezone = currentTimezone.substring(0, 3) + ":" + currentTimezone.substring(3, 5);
                }

                return currentValue;
            }
        }
        return null;
    }

    /**
     * O metodo irá tentar pegar o timezone atual da requisição
     * caso não exista por padrão irá retornar o do servidor
     */
    public String getCurrentTimezoneFromRequestOrServer() {
        final String curretTimezone = this.getCurrentValue();
        if (curretTimezone != null) {
            return curretTimezone;
        }
        return this.getCurrenteTimeZoneFromServer();
    }

    @Override
    public boolean containsValidValue() {
        return !isEmpty(this.currentValue);
    }

    protected boolean isValid() {
        return TimeZoneDocument.isValidTimeZone(this.currentValue);
    }

    public TimeZoneDocument getCurrentTimezoneDocument() {
        final String timeZoneServer = getCurrenteTimeZoneFromServer();
        return new TimeZoneDocument()
                .setCurrentTimeZone(getCurrentValue())
                .setCreateTimeZone(getCurrentValue())
                .setServerCurrentTimeZone(timeZoneServer)
                .setServerCreteTimeZone(timeZoneServer);
    }


    public String getCurrenteTimeZoneFromServer() {
        final TimeZone tz = TimeZone.getDefault();
        return getTimezoneFromId(tz.getID());
    }
}
