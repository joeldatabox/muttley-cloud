package br.com.muttley.security.infra.service.impl;

import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserPreferencesResolverEvent;
import br.com.muttley.model.security.events.UserPreferencesResolverEvent.UserPreferencesResolverEventItem;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.feign.OwnerServiceClient;
import br.com.muttley.security.feign.UserPreferenceServiceClient;
import br.com.muttley.security.infra.service.LocalUserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira 24/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class LocalUserPrefenceServiceImpl implements LocalUserPreferenceService {
    private final RedisService redisService;
    private final UserPreferenceServiceClient userPreferenceService;
    private final OwnerServiceClient ownerService;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public LocalUserPrefenceServiceImpl(final RedisService redisService, final UserPreferenceServiceClient userPreferenceService, OwnerServiceClient ownerService, ApplicationEventPublisher publisher) {
        this.redisService = redisService;
        this.userPreferenceService = userPreferenceService;
        this.ownerService = ownerService;
        this.publisher = publisher;
    }

    @Override
    public UserPreferences getUserPreferences(final JwtToken jwtUser, final User user) {
        if (jwtUser.getUsername().equals(user.getUserName())) {
            final UserPreferences preferences;
            //verificando se já existe a chave
            if (this.redisService.hasKey(this.getBasicKey(user))) {
                //recuperando as preferencias do usuário do cache
                preferences = (UserPreferences) this.redisService.get(this.getBasicKey(user));
            } else {
                //se chegou aqui é sinal que ainda não exite um preferencia carregada em cache, logo devemo buscar
                //recuperando as preferencias do servidor de segurança
                preferences = userPreferenceService.getUserPreferences();
                if (preferences != null) {
                    //por comodidade vamo disparar o evento para resolução dos itens das preferencias
                    final UserPreferencesResolverEvent event = new UserPreferencesResolverEvent(new UserPreferencesResolverEventItem(user, preferences));
                    this.publisher.publishEvent(event);
                    //salvando no cache
                    this.redisService.set(this.getBasicKey(user), preferences, jwtUser.getExpiration());
                }
            }
            return preferences;
        }
        throw new MuttleySecurityBadRequestException(UserPreferences.class, "user", "O usuário informado é diferente do presente no token");
    }

    @Override
    public void expireUserPreferences(User user) {
        //deletando item do cache
        this.redisService.delete(this.getBasicKey(user));
    }

    private String getBasicKey(final User user) {
        return BASIC_KEY + user.getId();
    }
}
