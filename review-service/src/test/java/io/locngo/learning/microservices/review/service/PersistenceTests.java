package io.locngo.learning.microservices.review.service;

import io.locngo.learning.microservices.review.service.persistence.ReviewEntity;
import io.locngo.learning.microservices.review.service.persistence.ReviewRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PersistenceTests {

    @Autowired
    private ReviewRepository reviewRepository;

    private ReviewEntity savedEntity;

    @Before
    public void setupDb() {
        reviewRepository.deleteAll();
        ReviewEntity entity = new ReviewEntity(1, 2, "a", "s", "c");
        savedEntity = reviewRepository.save(entity);

        assertEquals(entity, savedEntity);
    }

    @Test
    public void create() {
        ReviewEntity newEntity = new ReviewEntity(1, 3, "a", "s", "c");

        reviewRepository.save(newEntity);

        ReviewEntity foundEntity = reviewRepository.findById(newEntity.getId()).get();
        assertEqualsReview(newEntity, foundEntity);

        assertEquals(2, reviewRepository.count());
    }

    @Test
    public void update() {
        savedEntity.setAuthor("a2");
        reviewRepository.save(savedEntity);

        ReviewEntity foundEntity = reviewRepository.findById(savedEntity.getId()).get();
        assertEquals(1, foundEntity.getVersion());
        assertEquals("a2", foundEntity.getAuthor());
    }

    @Test
    public void delete() {
        reviewRepository.delete(savedEntity);
        assertFalse(reviewRepository.existsById(savedEntity.getId()));
    }

    @Test
    public void getReviewsByProductId() {
        List<ReviewEntity> entityList = reviewRepository.findByProductId(savedEntity.getProductId());

        assertEquals(1, entityList.size());
        assertEqualsReview(entityList.get(0), savedEntity);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void duplicateError() {
        ReviewEntity entity = new ReviewEntity(1, 2, "a", "s", "c");
        reviewRepository.save(entity);
    }

    @Test
    public void optimisticLockError() {
        ReviewEntity entity1 = reviewRepository.findById(savedEntity.getId()).get();
        ReviewEntity entity2 = reviewRepository.findById(savedEntity.getId()).get();

        entity1.setAuthor("a1");
        reviewRepository.save(entity1);

        try {
            entity2.setAuthor("a2");
            reviewRepository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException exception) {}

        ReviewEntity updatedEntity = reviewRepository.findById(savedEntity.getId()).get();
        assertEquals(1, updatedEntity.getVersion());
        assertEquals("a1", updatedEntity.getAuthor());
    }

    private void assertEqualsReview(ReviewEntity expectedEntity, ReviewEntity actualEntity) {
        assertEquals(expectedEntity.getId(),        actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),   actualEntity.getVersion());
        assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
        assertEquals(expectedEntity.getReviewId(),  actualEntity.getReviewId());
        assertEquals(expectedEntity.getAuthor(),    actualEntity.getAuthor());
        assertEquals(expectedEntity.getSubject(),   actualEntity.getSubject());
        assertEquals(expectedEntity.getContent(),   actualEntity.getContent());
    }
}
