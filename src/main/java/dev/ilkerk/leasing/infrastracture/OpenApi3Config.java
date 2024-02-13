package dev.ilkerk.leasing.infrastracture;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApi3Config {
    @Value("${spring.webflux.base-path}")
    String servicePath;

    @Value("${com.api.host}")
    String apiHost;

    @Value("${com.name}")
    String name;

    @Value("${com.email}")
    String email;

    @Value("${com.api.doc.title}")
    String docTitle;

    @Value("${com.api.doc.description}")
    String docDescription;

    @Value("${application.version}")
    String applicationVersion;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Authorization", new SecurityScheme()
                                .name("Authorization")
                                .in(SecurityScheme.In.HEADER)
                                .type(SecurityScheme.Type.APIKEY)
                                .description("Api Key for Authorization")))
                .security(Collections.singletonList(new SecurityRequirement().addList("Authorization")))
                .servers(Collections.singletonList(new Server().url(apiHost + servicePath)))
                .info(new Info()
                        .title(docTitle)
                        .description(docDescription)
                        .version(applicationVersion)
                        .contact(new Contact()
                                .name(name)
                                .url(apiHost)
                                .email(email)));
    }
}
