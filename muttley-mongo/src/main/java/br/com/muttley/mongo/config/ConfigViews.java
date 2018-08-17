package br.com.muttley.mongo.config;

import br.com.muttley.mongo.config.source.ViewRepository;
import br.com.muttley.mongo.config.source.ViewSource;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 18/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class ConfigViews implements ApplicationListener<ContextRefreshedEvent> {
    private final String dbName;
    private final ViewRepository viewRepository;
    private final MongoClient client;

    @Autowired
    public ConfigViews(@Value("${spring.data.mongodb.database}") final String dbName, final ViewRepository viewRepository, final MongoClient client) {
        this.dbName = dbName;
        this.viewRepository = viewRepository;
        this.client = client;
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {

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
    }
}
