package io.locngo.learning.microservices.product.composite.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@EnableSwagger2
@SpringBootApplication
@ComponentScan(value = "io.locngo.learning.microservices")
public class ProductCompositeApplication {

    @Value("${api.common.version}")
    private String apiVersion;

    @Value("${api.common.title}")
    private String apiTitle;

    @Value("${api.common.description}")
    private String apiDescription;

    @Value("${api.common.termsOfServiceUrl}")
    private String apiTermsOfServiceUrl;

    @Value("${api.common.license}")
    private String apiLicense;

    @Value("${api.common.licenseUrl}")
    private String apiLicenseUrl;

    @Value("${api.common.contact.name}")
    private String apiContactName;

    @Value("${api.common.contact.url}")
    private String apiContactUrl;

    @Value("${api.common.contact.email}")
    private String apiContactEmail;

    public static void main(String[] args) {
        SpringApplication.run(ProductCompositeApplication.class);
    }

    /**
     * Will expose on $HOST:$PORT/swagger-ui.html
     * @return
     */
    @Bean
    public Docket apiDocumentation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.locngo.learning.microservices.product.composite.service"))
                .paths(PathSelectors.any())
                .build()
                .globalResponseMessage(RequestMethod.POST, Collections.emptyList())
                .globalResponseMessage(RequestMethod.GET, Collections.emptyList())
                .globalResponseMessage(RequestMethod.DELETE, Collections.emptyList())
                .apiInfo(
                    new ApiInfo(
                            apiTitle,
                            apiDescription,
                            apiVersion,
                            apiTermsOfServiceUrl,
                            new Contact(apiContactName, apiContactUrl, apiContactEmail),
                            apiLicense,
                            apiLicenseUrl,
                            Collections.emptyList()
                    )
                );
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
