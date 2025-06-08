package br.ueg.desenvolvimento.web.task_do.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        // Configuração do resolvedor de visualização JSP
        InternalResourceViewResolver jspResolver = new InternalResourceViewResolver();
        jspResolver.setPrefix("/WEB-INF/views/");
        jspResolver.setSuffix(".jsp");
        jspResolver.setViewClass(JstlView.class);
        jspResolver.setOrder(1);
        registry.viewResolver(jspResolver);
        
        // O Thymeleaf já está configurado automaticamente pelo Spring Boot
    }
}
