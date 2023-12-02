package step.learning.ioc;

import com.google.inject.AbstractModule;
import step.learning.ws.WebsocketConfigurator;

public class WebsocketModule extends AbstractModule {
    @Override
    protected void configure() {
        // інжекція у статичні поля
        requestStaticInjection(WebsocketConfigurator.class);
    }
}
