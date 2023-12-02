package step.learning.ioc;

import com.google.inject.Guice;
import com.google.inject.servlet.ServletModule;
import step.learning.filters.CharsetFilter;
import step.learning.servlets.*;

public class RouterModule extends ServletModule {
    @Override
    protected void configureServlets() {
        // третій спосіб конфігурування фільтрів та сервлетів - IoC
        filter("/*").through(CharsetFilter.class);

        serve("/"       ).with(HomeServlet.class);
        serve("/auth"   ).with(AuthServlet.class);
        serve("/db"     ).with(DbServlet.class);
        serve("/jsp"    ).with(JspServlet.class);
        serve("/filters").with(FiltersServlet.class);
        serve("/ioc"    ).with(IocServlet.class);
        serve("/signup" ).with(SignupServlet.class);
        serve("/spa"    ).with(SpaServlet.class);
        serve("/tpl/*"  ).with(TemplatesServlet.class);
        serve("/ws"  ).with(WsServlet.class);
    }
}
