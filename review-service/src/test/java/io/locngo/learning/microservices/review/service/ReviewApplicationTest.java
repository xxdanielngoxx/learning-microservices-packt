package io.locngo.learning.microservices.review.service;

import io.locngo.learning.microservices.api.core.review.Review;
import io.locngo.learning.microservices.review.service.persistence.ReviewRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static reactor.core.publisher.Mono.just;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:review-db"
        }
)
public class ReviewApplicationTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ReviewRepository reviewRepository;

    @Before
    public void setupDb() {
        reviewRepository.deleteAll();
    }

    @Test
    public void getReviewsByProductId() {
        final int productId = 1;

        assertEquals(0, reviewRepository.findByProductId(1).size());

        postAndVerifyReview(productId, 1, HttpStatus.OK);
        postAndVerifyReview(productId, 2, HttpStatus.OK);
        postAndVerifyReview(productId, 3, HttpStatus.OK);

        assertEquals(3, reviewRepository.findByProductId(productId).size());

        getAndVerifyReviewsByProductId(productId, HttpStatus.OK)
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[2].productId").isEqualTo(1)
                .jsonPath("$[2].reviewId").isEqualTo(3);
    }

    @Test
    public void duplicateError() {
        final int productId = 1;
        final int reviewId = 1;

        assertEquals(0, reviewRepository.count());

        postAndVerifyReview(productId, reviewId, HttpStatus.OK)
                .jsonPath("$.productId").isEqualTo(productId)
                .jsonPath("$.reviewId").isEqualTo(reviewId);

        assertEquals(1, reviewRepository.count());

        postAndVerifyReview(productId, reviewId, HttpStatus.UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/reviews")
                .jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Review Id: 1");

        assertEquals(1, reviewRepository.count());
    }

    @Test
    public void deleteReviews() {
        final int productId = 1;
        final int reviewId = 1;

        assertEquals(0, reviewRepository.findByProductId(productId).size());

        postAndVerifyReview(productId, reviewId, HttpStatus.OK);
        assertEquals(1, reviewRepository.findByProductId(productId).size());

        deleteAndVerifyReviewsByProductId(productId, HttpStatus.OK);
        assertEquals(0, reviewRepository.findByProductId(productId).size());

        deleteAndVerifyReviewsByProductId(productId, HttpStatus.OK);
    }

    @Test
    public void getReviewsMissingParameter() {
        getAndVerifyReviewsByProductId("", HttpStatus.BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/reviews")
                .jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present");
    }

    @Test
    public void getRecommendationsInvalidParameter() {
        getAndVerifyReviewsByProductId("?productId=no-integer", HttpStatus.BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/reviews")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getReviewsNotFound() {
        final int productId = 213;

        getAndVerifyReviewsByProductId("?productId=" + productId, HttpStatus.OK)
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getReviewsInvalidParameterNegativeValue() {
        final int productId = -1;

       getAndVerifyReviewsByProductId("?productId=" + productId, HttpStatus.UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/reviews")
                .jsonPath("$.message").isEqualTo("Invalid productId: " + productId);
    }

    private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(int productId, HttpStatus expectedStatus) {
        return getAndVerifyReviewsByProductId("?productId=" + productId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(String productIdQuery, HttpStatus expectedStatus) {
        return client.get()
                .uri("/reviews" + productIdQuery)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec postAndVerifyReview(int productId, int reviewId, HttpStatus expectedStatus) {
        Review review = new Review(productId, reviewId, "Author " + reviewId, "Subject " + reviewId, "Content " + reviewId, "SA");
        return client.post()
                .uri("/reviews")
                .body(just(review), Review.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec deleteAndVerifyReviewsByProductId(int productId, HttpStatus expectedStatus) {
        return client.delete()
                .uri("reviews?productId=" + productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody();
    }
}