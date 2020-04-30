package br.com.muttley.hermes.server.service;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.hermes.notification.TokenId;
import br.com.muttley.model.hermes.notification.UserTokensNotification;
import br.com.muttley.model.security.User;


public interface UserTokensNotificationService extends Service<UserTokensNotification> {
    UserTokensNotification findByUser(final User user);

    void addTokenNotification(final User user, final TokenId tokenId);
}
