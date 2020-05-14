package com.country.neighbours.visit.configuration;


import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//@EnableSwagger2
public class SwaggerConfig {

    /**
     * @Bean public Docket api() { return new Docket(DocumentationType.SWAGGER_2) .select()
     * .apis(RequestHandlerSelectors.basePackage("com.country.neighbours.visit.controller"))
     * .paths(PathSelectors.ant("/request")) .build(); }
     */

    //@Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
                registry.addResourceHandler("/webjars/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/");
            }
        };
    }
}