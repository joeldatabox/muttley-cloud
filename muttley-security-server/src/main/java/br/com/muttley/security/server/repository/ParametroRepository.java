package br.com.muttley.security.server.repository;


import br.com.muttley.model.parametrizacao.Parametro;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Carolina Cedro on 14/05/25.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com.br">ana.carolina@maxxsoft.com.br</a>
 * @project agrifocus-cloud
 */
@Repository
public interface ParametroRepository extends DocumentMongoRepository<Parametro> {


}

