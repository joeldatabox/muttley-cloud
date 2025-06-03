package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.service.impl.ModelSyncServiceImpl;
import br.com.muttley.model.parametrizacao.ParametrizacaoToggle;
import br.com.muttley.model.parametrizacao.Parametro;
import br.com.muttley.model.security.User;
import br.com.muttley.security.server.repository.ToggleClienteRepository;
import br.com.muttley.security.server.service.ToggleClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * @author Carolina Cedro on 14/05/25.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com.br">ana.carolina@maxxsoft.com.br</a>
 * @project agrifocus-cloud
 */
@Service
public class ToogleClienteServiceImpl extends ModelSyncServiceImpl<ParametrizacaoToggle> implements ToggleClienteService {

    private static final String[] basicRoles = new String[]{"parametrizacao"};
    
    private final ToggleClienteRepository repository;

    @Override
    public String[] getBasicRoles() {
        return basicRoles;
    }

    @Autowired
    public ToogleClienteServiceImpl(
            final ToggleClienteRepository repository,
            final MongoTemplate mongoTemplate
    ) {
        super(repository, ParametrizacaoToggle.class, mongoTemplate);
        this.repository = repository;
    }

    /**
     * Busca uma parametrização pelo ID da empresa
     */
    public Optional<ParametrizacaoToggle> buscarPorUser(final User user) {
        return repository.findByUserId(user);
    }

    /**
     * Busca o valor de um parâmetro específico de uma empresa
     */
    public Optional<String> getValorParametro(final User user, final String chave) {
        return buscarPorUser(user)
                .flatMap(parametrizacao -> parametrizacao.getParametros().stream()
                        .filter(p -> p.getChave().equalsIgnoreCase(chave))
                        .findFirst()
                        .map(Parametro::getChave));
    }

    /**
     * Adiciona ou atualiza um parâmetro na lista
     */
    public ParametrizacaoToggle salvarOuAtualizarParametro(final User user, final Parametro parametro) {
        ParametrizacaoToggle toggle = repository.findByUserId(user)
                .orElseGet(() -> new ParametrizacaoToggle()
                        .setUser(user)
                        .setSync("sync-" + user)
                        .setDtSync(new Date()));

        // Remove se já existe com essa chave
        toggle.getParametros().removeIf(p -> p.getChave().equalsIgnoreCase(parametro.getChave()));

        // Adiciona o novo
        toggle.getParametros().add(parametro);

        // Atualiza data de sync
        toggle.setDtSync(new Date());

        return repository.save(toggle);
    }
}

