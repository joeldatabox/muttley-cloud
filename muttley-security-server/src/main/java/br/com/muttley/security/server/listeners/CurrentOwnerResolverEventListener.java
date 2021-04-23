package br.com.muttley.security.server.listeners;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.headers.components.MuttleyRequestHeader;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.security.server.events.CurrentOwnerResolverEvent;
import br.com.muttley.security.server.service.AdminOwnerService;
import br.com.muttley.security.server.service.OwnerService;
import br.com.muttley.security.server.service.UserDataBindingService;
import br.com.muttley.security.server.service.UserPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static br.com.muttley.model.security.preference.UserPreferences.OWNER_PREFERENCE;

/**
 * @author Joel Rodrigues Moreira 20/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class CurrentOwnerResolverEventListener implements ApplicationListener<CurrentOwnerResolverEvent> {

    private final UserPreferencesService preferencesService;
    private final UserDataBindingService dataBindingService;
    private final OwnerService ownerService;
    private final AdminOwnerService adminOwnerService;
    @Autowired
    private MuttleyRequestHeader requestHeader;

    @Autowired
    public CurrentOwnerResolverEventListener(final UserPreferencesService preferencesService, final UserDataBindingService dataBindingService, final OwnerService ownerService, final AdminOwnerService adminOwnerService) {
        this.preferencesService = preferencesService;
        this.dataBindingService = dataBindingService;
        this.ownerService = ownerService;
        this.adminOwnerService = adminOwnerService;
    }

    @Override
    public void onApplicationEvent(final CurrentOwnerResolverEvent event) {
        final String ownerId;
        //verificando se as preferencias foram carregadas
        if (!event.getSource().preferencesIsEmpty() || !event.getSource().getPreferences().contains(OWNER_PREFERENCE)) {
            //verificando se existe uma preferencia de owner para ser carregada
            if (preferencesService.containsPreference(event.getSource(), OWNER_PREFERENCE)) {
                ownerId = preferencesService.getPreference(event.getSource(), OWNER_PREFERENCE).getValue().toString();
            } else {
                try {
                    //se chegou até aqui é sinal que não tem nenhum owner para o usuário corrente
                    //logo vamo ver se conseguimos encontrar um owner

                    final List<? extends OwnerData> owners;
                    //é uma requisição do serviço de admin?
                    if (this.requestHeader.isRequestFromAdminServer()) {
                        owners = adminOwnerService.loadOwnersOfUser(event.getSource());
                    } else {
                        owners = ownerService.loadOwnersOfUser(event.getSource());
                    }
                    //pegando o primeiro owner e salvando o mesmo na preferencia
                    final Preference preference = new Preference(OWNER_PREFERENCE, owners.get(0).getId());
                    //salvando nas preferencias do usuario
                    this.preferencesService.setPreference(event.getSource(), preference);
                    //setando a preferencia criada
                    event.getSource().getPreferences().set(preference);
                    event.setOwnerResolved(owners.get(0));
                } catch (MuttleyNoContentException exception) {
                    throw new MuttleySecurityUnauthorizedException("O usuário não está presente em nenhuma base. Entre em contato com o administrador");
                    //se chegou aqui quer dizer que é uma requisição do odin
                }
                return;
                //ownerId = preference.getValue().toString();
            }
        } else {
            //se chegou até aqui é sinal que tem onwer na preferencia. Logo basta carregar o mesmo
            ownerId = event.getSource().getPreferences().get(OWNER_PREFERENCE).getValue().toString();
        }
        //carregando o owner
        try {
            event.setOwnerResolved(this.ownerService.findByUserAndId(event.getSource(), ownerId));
        } catch (final MuttleyBadRequestException ex) {
            //se chegou aqui é sinal que o usuário não está em nenhuma base de usuário
            //logo podemos remover
            preferencesService.removePreference(event.getSource(), OWNER_PREFERENCE);
        }
    }
}
