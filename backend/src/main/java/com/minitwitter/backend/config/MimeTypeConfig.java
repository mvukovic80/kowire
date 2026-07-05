package com.minitwitter.backend.config;

import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MimeTypeConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        // Explicitly force .css and .js files to register with correct headers
        mappings.add("css", "text/css;charset=UTF-8");
        mappings.add("js", "application/javascript;charset=UTF-8");
        factory.setMimeMappings(mappings);
    }
}
