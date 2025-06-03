package br.com.muttley.security.server.controller;

import br.com.muttley.model.parametrizacao.ParametrizacaoToggle;
import br.com.muttley.model.parametrizacao.Parametro;
import br.com.muttley.model.security.User;
import br.com.muttley.rest.AbstractModelSyncRestController;
import br.com.muttley.security.server.service.AuthService;
import br.com.muttley.security.server.service.ToggleClienteService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Carolina Cedro
 * @since 14/05/2025
 */
@RestController
@RequestMapping(value = "/api/v1/parametrizacoes", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class ToggleClienteController extends AbstractModelSyncRestController<ParametrizacaoToggle> {

    private final ToggleClienteService service;

    @Autowired
    public ToggleClienteController(
            final ToggleClienteService service,
            final UserService userService,
            final ApplicationEventPublisher eventPublisher
    ) {
        super(service, userService, eventPublisher);
        this.service = service;
    }

    /**
     * Lista todos os parâmetros de uma empresa
     */
    @GetMapping("/empresa/{user}")
    public ResponseEntity<ParametrizacaoToggle> buscarPorUser(@PathVariable User user) {
        return service.buscarPorUser(user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Busca o valor de uma chave específica
     */
    @GetMapping("/empresa/{user}/chave/{chave}")
    public ResponseEntity<String> buscarPorChave(
            @PathVariable User user,
            @PathVariable String chave
    ) {
        return service.getValorParametro(user, chave)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Adiciona ou atualiza um parâmetro
     */
    @PostMapping("/empresa/{user}")
    public ResponseEntity<ParametrizacaoToggle> adicionarOuAtualizarParametro(
            @PathVariable User user,
            @RequestBody Parametro parametro
    ) {
        ParametrizacaoToggle atualizado = service.salvarOuAtualizarParametro(
                user,
                parametro
        );
        return ResponseEntity.ok(atualizado);
    }

}
