package br.com.muttley.model.autoconfig;


import br.com.muttley.model.events.SpelResolveEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author Joel Rodrigues Moreira on 08/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
//@Component
public class SpelResolveEventListener implements ApplicationListener<SpelResolveEvent> {
    private final ApplicationContext context;

    public SpelResolveEventListener(final ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(SpelResolveEvent event) {
        final StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();

        standardEvaluationContext.addPropertyAccessor(new BeanFactoryAccessor());
        standardEvaluationContext.setBeanResolver(new BeanFactoryResolver(this.context));
        standardEvaluationContext.setRootObject(this.context);

        final Expression expression = new SpelExpressionParser().parseExpression(event.getSource(), ParserContext.TEMPLATE_EXPRESSION);
        event.setResolved(expression.getValue(this.context, String.class));
        event.setExpression(expression);


/*


        context.addPropertyAccessor(new BeanFactoryAccessor());
        context.setBeanResolver(new BeanFactoryResolver(this.context));
        context.setRootObject(this.context);


        Expression expression = new SpelExpressionParser().parseExpression("#{documentNameConfig.getNameCollectionOwner()}", ParserContext.TEMPLATE_EXPRESSION);
        System.out.println(expression.getValue(context, String.class));*/
    }
}
