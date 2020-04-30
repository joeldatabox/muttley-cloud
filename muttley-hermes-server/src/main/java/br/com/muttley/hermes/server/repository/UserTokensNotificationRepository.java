package br.com.muttley.hermes.server.repository;

import br.com.muttley.model.hermes.notification.UserTokensNotification;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Joel Rodrigues Moreira on 30/04/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project agrifocus-cloud
 */
@Repository
public interface UserTokensNotificationRepository extends DocumentMongoRepository<UserTokensNotification> {
    UserTokensNotification findByUser(final User user);
}
