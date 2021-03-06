package br.com.muttley.hermes.server.service;

import br.com.muttley.domain.Service;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.hermes.notification.TokenId;
import br.com.muttley.model.hermes.notification.UserTokensNotification;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserView;

/**
 * @author Joel Rodrigues Moreira on 04/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface UserTokensNotificationService extends Service<UserTokensNotification> {
    UserTokensNotification findByUser(final User user) throws MuttleyNotFoundException;

    UserTokensNotification findByUser(final UserView user) throws MuttleyNotFoundException;

    UserTokensNotification findByUser(final String userId) throws MuttleyNotFoundException;

    void addTokenNotification(final User user, final TokenId tokenId);
}
