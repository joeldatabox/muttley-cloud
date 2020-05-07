package br.com.muttley.hermes.server.service;

import br.com.muttley.domain.service.Service;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.hermes.notification.TokenId;
import br.com.muttley.model.hermes.notification.UserTokensNotification;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserView;


public interface UserTokensNotificationService extends Service<UserTokensNotification> {
    UserTokensNotification findByUser(final User user) throws MuttleyNotFoundException;

    UserTokensNotification findByUser(final UserView user) throws MuttleyNotFoundException;

    UserTokensNotification findByUser(final String userId) throws MuttleyNotFoundException;

    void addTokenNotification(final User user, final TokenId tokenId);
}
