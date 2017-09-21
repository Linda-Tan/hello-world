package com.junliang.spring.config;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("business-api")
                .select() //Ignores controllers annotated with @CustomIgnore
                .apis(RequestHandlerSelectors.basePackage("com.junliang.spring.restcontrol"))
                // .pathMapping(paths())// base，最终调用接口后会和paths拼接在一起
                .paths(PathSelectors.any())//过滤的接口
                .build()
                //.securitySchemes(securitySchemes())
                //.securityContext(securityContext())
                .apiInfo(testApiInfo());
    }
    private Predicate<String> paths() {
        return or(
                regex("/business.*"),
                regex("/some.*"),
                regex("/contacts.*"),
                regex("/pet.*"),
                regex("/springsRestController.*"),
                regex("/test.*"));
    }

    private ApiInfo testApiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger2 Title ")//大标题
                .description("spring boot beta")//详细描述
                .version("1.0 Beta")//版本
                .termsOfServiceUrl("https://github.com/Linda-Tan/spring-boot")
                .license("The Apache License, Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .contact(new Contact("Tony.li","@li926893","li926893@gmail.com"))
                .build();
    }
}
