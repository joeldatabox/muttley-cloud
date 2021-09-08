package br.com.muttley.mongo.service.config;

import br.com.muttley.model.View;
import br.com.muttley.mongo.service.config.source.ViewSource;
import br.com.muttley.mongo.service.event.ConfigViewContextEvent;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 18/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class ConfigViews implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${spring.data.mongodb.database}")
    private String dbName;
    private final MongoTemplate template;
    private final MongoClient client;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public ConfigViews(MongoTemplate template, MongoClient client, ApplicationEventPublisher publisher) {
        this.template = template;
        this.client = client;
        this.publisher = publisher;
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        final ConfigViewContextEvent configViewContextEvent = new ConfigViewContextEvent();
        publisher.publishEvent(configViewContextEvent);
        configViewContextEvent.getSource().forEach(it -> this.createView(it));

    }

    private void createView(final ViewSource source) {
        /**
         //verificando se a view existe
         final AbstractView view = this.viewRepository.findByName(source.getViewName());
         if (view == null) {
         //a view não existe, logo devemos criar a mesma
         this.client
         .getDatabase(dbName)
         .createView(source.getViewName(), source.getViewOn(), source.getPipeline());
         //salvando informações da view criada
         this.viewRepository.save(new View(source));
         } else {
         //se a view já existe devemos verificar a versão da mesma
         //se a versão for diferente devemos dropar essa view
         if (!view.getVersion().equals(source.getVersion())) {
         this.client
         .getDatabase(DB_NAME)
         .getCollection(source.getViewName())
         .drop();

         //adicionando novamente a view
         this.client
         .getDatabase(DB_NAME)
         .createView(source.getViewName(), source.getViewOn(), source.getPipeline());

         //atualizando info da view
         this.repository.save(view.updateInfo(source));
         }
         }*/
        final Query query = new Query();
        query.addCriteria(where("name").is(source.getViewName()));
        //verificando se a view existe
        final View view = this.template.findOne(new Query(where("name").is(source.getViewName())), View.class);

        if (view == null) {
            //a view não existe, logo devemos criar a mesma
            this.client
                    .getDatabase(this.dbName)
                    .createView(source.getViewName(), source.getViewOn(), source.getPipeline());
            //salvando informações da view criada
            this.template.save(new View(source.getViewName(), source.getVersion(), source.getDescription()));
        } else {
            //se a view já existe devemos verificar a versão da mesma
            //se a versão for diferente devemos dropar essa view
            if (!view.getVersion().equals(source.getVersion())) {
                this.client
                        .getDatabase(this.dbName)
                        .getCollection(source.getViewName())
                        .drop();

                //adicionando novamente a view
                this.client
                        .getDatabase(this.dbName)
                        .createView(source.getViewName(), source.getViewOn(), source.getPipeline());

                //atualizando info da view
                //this.template.save(view.updateInfo(source));
                this.template.save(view.setDescription(source.getDescription())
                        .setVersion(source.getVersion()));
            }
        }
    }
}
