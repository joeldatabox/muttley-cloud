package br.com.muttley.security.server.postinit;

import br.com.muttley.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 10/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Component
public class ClearRedis implements ApplicationListener<ContextRefreshedEvent> {
    final RedisService service;

    @Autowired
    public ClearRedis(RedisService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.service.clearAll();
    }
}
