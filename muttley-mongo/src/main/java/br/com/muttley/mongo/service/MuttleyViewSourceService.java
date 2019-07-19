package br.com.muttley.mongo.service;

import br.com.muttley.mongo.views.source.ViewSource;

/**
 * Interface de serviço para facílitar a customização de views
 *
 * @author Joel Rodrigues Moreira on 15/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyViewSourceService {
    ViewSource[] getCustomViewSource();
}
