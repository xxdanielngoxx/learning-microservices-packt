package io.locngo.learning.microservices.recommendation.service;

import io.locngo.learning.microservices.recommendation.service.persistence.RecommendationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

@SpringBootApplication
@ComponentScan(value = "io.locngo.learning.microservices")
public class RecommendationApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationApplication.class);

    @Autowired
    private MongoOperations mongoTemplate;

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(RecommendationApplication.class);

        String mongoDbHost = context.getEnvironment().getProperty("spring.data.mongodb.host");
        String mongoDbPort = context.getEnvironment().getProperty("spring.data.mongodb.port");
        LOGGER.info("Connected to MongoDB: " + mongoDbHost + ":" + mongoDbPort);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initIndicesAfterStartup() {
        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate.getConverter().getMappingContext();
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

        IndexOperations indexOperations = mongoTemplate.indexOps(RecommendationEntity.class);
        resolver.resolveIndexFor(RecommendationEntity.class).forEach(indexOperations::ensureIndex);
    }
}
