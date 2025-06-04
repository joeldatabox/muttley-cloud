package br.com.muttley.security.server.service.impl;

import br.com.muttley.model.parametrizacao.Parametro;
import br.com.muttley.security.server.repository.ParametroRepository;
import br.com.muttley.security.server.service.ParametroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;


/**
 * @author Carolina Cedro on 14/05/25.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com.br">ana.carolina@maxxsoft.com.br</a>
 * @project agrifocus-cloud
 */
@Service
public class ParametroServiceImpl extends SecurityServiceImpl<Parametro> implements ParametroService {

    private final ParametroRepository repository;


    @Autowired
    public ParametroServiceImpl(
            final ParametroRepository repository,
            final MongoTemplate mongoTemplate
    ) {
        super(repository, mongoTemplate, Parametro.class);
        this.repository = repository;
    }


}

