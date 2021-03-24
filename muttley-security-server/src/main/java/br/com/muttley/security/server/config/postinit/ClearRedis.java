package br.com.muttley.security.server.config.postinit;

import br.com.muttley.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira 24/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class ClearRedis implements ApplicationListener<ApplicationReadyEvent> {
    final RedisService service;

    @Autowired
    public ClearRedis(RedisService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        this.service.clearAll();
    }
}

