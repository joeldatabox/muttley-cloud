package br.com.muttley.security.infra.service.impl;

import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.localcache.services.LocalUserPreferenceService;
import br.com.muttley.localcache.services.impl.AbstractLocalUserPrefenceServiceImpl;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.DeserializeUserPreferencesEvent;
import br.com.muttley.model.security.events.DeserializeUserPreferencesEvent.UserPreferencesResolverEventItem;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.OwnerServiceClient;
import br.com.muttley.security.feign.UserPreferenceServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira 24/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class LocalUserPrefenceServiceImpl extends AbstractLocalUserPrefenceServiceImpl implements LocalUserPreferenceService {
    private final UserPreferenceServiceClient userPreferenceService;
    private final OwnerServiceClient ownerService;

    @Autowired
    public LocalUserPrefenceServiceImpl(final RedisService redisService, final UserPreferenceServiceClient userPreferenceService, OwnerServiceClient ownerService, ApplicationEventPublisher publisher) {
        super(redisService, publisher);
        this.userPreferenceService = userPreferenceService;
        this.ownerService = ownerService;
    }

    @Override
    public UserPreferences getUserPreferences(final JwtToken jwtUser, final User user) {
        if (jwtUser.getUsername().equals(user.getUserName())) {
            final UserPreferences preferences;
            //verificando se já existe a chave
            if (this.redisService.hasKey(this.getBasicKey(user))) {
                //recuperando as preferencias do usuário do cache
                preferences = this.getPreferenceInCache(jwtUser, user);
            } else {
                //se chegou aqui é sinal que ainda não exite um preferencia carregada em cache, logo devemo buscar
                //recuperando as preferencias do servidor de segurança
                preferences = userPreferenceService.getUserPreferences();
                //salvando no cache para evitar loops desnecessários
                this.savePreferenceInCache(jwtUser, user, preferences);
            }
            if (preferences != null) {
                //por comodidade vamo disparar o evento para resolução dos itens das preferencias
                final DeserializeUserPreferencesEvent event = new DeserializeUserPreferencesEvent(new UserPreferencesResolverEventItem(user, preferences));
                this.publisher.publishEvent(event);
                //salvando no cache novamente para guardar as modificações
                this.savePreferenceInCache(jwtUser, user, preferences);
            }
            return preferences;
        }
        throw new MuttleySecurityBadRequestException(UserPreferences.class, "user", "O usuário informado é diferente do presente no token");
    }

}
