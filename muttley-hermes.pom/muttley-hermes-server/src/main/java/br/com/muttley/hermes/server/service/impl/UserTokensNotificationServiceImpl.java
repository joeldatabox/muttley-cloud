package br.com.muttley.hermes.server.service.impl;

import br.com.muttley.domain.impl.ServiceImpl;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.hermes.server.repository.UserTokensNotificationRepository;
import br.com.muttley.hermes.server.service.UserTokensNotificationService;
import br.com.muttley.model.hermes.notification.TokenId;
import br.com.muttley.model.hermes.notification.UserTokensNotification;
import br.com.muttley.model.security.User;
import br.com.muttley.redis.service.RedisService;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 04/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class UserTokensNotificationServiceImpl extends ServiceImpl<UserTokensNotification> implements UserTokensNotificationService {
    private final UserTokensNotificationRepository repository;
    private final RedisService redisService;
    //tempo de validade do token 1000 * 60 * 60 * 24 * 10 = dias
    private final long expiration = 864000000;

    @Autowired
    public UserTokensNotificationServiceImpl(final UserTokensNotificationRepository repository, final MongoTemplate mongoTemplate, final RedisService redisService) {
        super(repository, mongoTemplate, UserTokensNotification.class);
        this.repository = repository;
        this.redisService = redisService;
    }

    @Override
    public UserTokensNotification findByUser(final User user) {
        //verificando se já tem no cache
        if (this.redisService.hasKey(this.generateTokenRedis(user))) {
            return (UserTokensNotification) this.redisService.get(this.generateTokenRedis(user));
        }
        final UserTokensNotification token = this.repository.findByUser(user);
        if (token == null) {
            throw new MuttleyNoContentException(UserTokensNotification.class, "user", "Nenhum registro encontrado");
        }
        //se chegou até aqui é sinal que o token ainda não está no cache
        //adicionando o token no cache
        this.redisService.set(this.generateTokenRedis(user), token, expiration);
        return token;
    }

    @Override
    public void addTokenNotification(final User user, final TokenId tokenId) {
        if (!this.repository.exists("user.$id", new ObjectId(user.getId()))) {
            this.save(user, new UserTokensNotification().setUser(user));
        } else {
            this.removeTokenIdFromAnotherUsers(user, tokenId);

        }
        this.redisService.delete(this.generateTokenRedis(user));


    }

    /**
     * Remove o token de outros usuários
     * Isso se faz necessário pois o token está relacionado ao aparelho e por conta disso
     * o usuário pode seder o aparelho para outros usuários.
     */
    private void removeTokenIdFromAnotherUsers(final User user, final TokenId tokenId) {
        this.mongoTemplate.updateFirst(
                new Query(
                        where("user.$id").ne(new ObjectId(user.getId()))
                                .and("tokens").elemMatch(where("token").is(tokenId.getToken()).and("origin").is(tokenId.getOrigin()))
                ),
                new Update()
                        .pull("tokens",
                                new BasicDBObject("origin", tokenId.getOrigin()).append("token", tokenId.getToken())
                        ),
                UserTokensNotification.class
        );
    }

    private String generateTokenRedis(final User user) {
        return user.getId() + ".tokensNotification";
    }
}
