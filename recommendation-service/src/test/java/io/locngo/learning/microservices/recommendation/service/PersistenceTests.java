package io.locngo.learning.microservices.recommendation.service;

import io.locngo.learning.microservices.recommendation.service.persistence.RecommendationEntity;
import io.locngo.learning.microservices.recommendation.service.persistence.RecommendationRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {

    @Autowired
    private RecommendationRepository recommendationRepository;

    private RecommendationEntity savedEntity;

    @Before
    public void setupDb() {
        recommendationRepository.deleteAll();

        RecommendationEntity entity = new RecommendationEntity(1, 2, "a", 3, "c");
        savedEntity = recommendationRepository.save(entity);

        assertEqualsRecommendation(entity, savedEntity);
    }

    @Test
    public void create() {
        RecommendationEntity newEntity = new RecommendationEntity(1, 3, "a", 3, "c");
        recommendationRepository.save(newEntity);

        RecommendationEntity foundEntity = recommendationRepository.findById(newEntity.getId()).get();
        assertEqualsRecommendation(newEntity, foundEntity);

        assertEquals(2, recommendationRepository.count());
    }

    @Test
    public void update() {
        savedEntity.setAuthor("a2");
        recommendationRepository.save(savedEntity);

        RecommendationEntity foundEntity = recommendationRepository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)foundEntity.getVersion());
        assertEquals("a2", foundEntity.getAuthor());
    }

    @Test
    public void delete() {
        recommendationRepository.delete(savedEntity);
        assertFalse(recommendationRepository.existsById(savedEntity.getId()));
    }

    @Test
    public void getRecommendationsById() {
        List<RecommendationEntity> entityList = recommendationRepository.findByProductId(savedEntity.getProductId());

        assertEquals(1, entityList.size());
        assertEqualsRecommendation(savedEntity, entityList.get(0));
    }

    @Test
    public void optimisticLockError() {
        RecommendationEntity entity1 = recommendationRepository.findById(savedEntity.getId()).get();
        RecommendationEntity entity2 = recommendationRepository.findById(savedEntity.getId()).get();

        entity1.setAuthor("a1");
        recommendationRepository.save(entity1);

        try {
            entity2.setAuthor("a2");
            recommendationRepository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException exception) {}

        RecommendationEntity updatedEntity = recommendationRepository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("a1", updatedEntity.getAuthor());
    }

    private void assertEqualsRecommendation(RecommendationEntity expectedEntity, RecommendationEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getProductId(),        actualEntity.getProductId());
        assertEquals(expectedEntity.getRecommendationId(), actualEntity.getRecommendationId());
        assertEquals(expectedEntity.getAuthor(),           actualEntity.getAuthor());
        assertEquals(expectedEntity.getRating(),           actualEntity.getRating());
        assertEquals(expectedEntity.getContent(),          actualEntity.getContent());
    }
}
