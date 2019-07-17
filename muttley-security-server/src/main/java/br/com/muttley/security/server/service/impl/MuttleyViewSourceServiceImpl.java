package br.com.muttley.security.server.service.impl;

import br.com.muttley.mongo.service.MuttleyViewSourceService;
import br.com.muttley.mongo.views.source.ViewSource;
import br.com.muttley.security.server.autoconfig.view.source.ViewMuttleyUsers;
import br.com.muttley.security.server.autoconfig.view.source.ViewMuttleyWorkTeam;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira on 16/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class MuttleyViewSourceServiceImpl implements MuttleyViewSourceService {
    @Override
    public ViewSource[] getCustomViewSource() {
        return new ViewSource[]{new ViewMuttleyUsers(), new ViewMuttleyWorkTeam()};
    }
}
