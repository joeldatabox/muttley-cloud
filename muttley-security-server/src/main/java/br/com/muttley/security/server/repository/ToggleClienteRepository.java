package br.com.muttley.security.server.repository;

import br.com.muttley.model.parametrizacao.ParametrizacaoToggle;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Carolina Cedro on 14/05/25.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com.br">ana.carolina@maxxsoft.com.br</a>
 * @project agrifocus-cloud
 */
@Repository
public interface ToggleClienteRepository extends DocumentMongoRepository<ParametrizacaoToggle> {

    Optional<ParametrizacaoToggle> findByUserId(User userView);

}
