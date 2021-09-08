package br.com.muttley.mongo.service.event;

import br.com.muttley.mongo.service.config.source.ViewSource;
import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 08/09/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ConfigViewContextEvent extends ApplicationEvent {
    private List<ViewSource> views;

    public ConfigViewContextEvent() {
        super("");
        this.views = new ArrayList();
    }

    public ConfigViewContextEvent add(ViewSource source) {
        this.views.add(source);
        return this;
    }

    @Override
    public List<ViewSource> getSource() {
        return this.views;
    }
}
