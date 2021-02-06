package io.locngo.learning.microservices.product.composite.service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.locngo.learning.microservices.api.core.product.Product;
import io.locngo.learning.microservices.api.core.product.ProductService;
import io.locngo.learning.microservices.api.core.recommendation.Recommendation;
import io.locngo.learning.microservices.api.core.recommendation.RecommendationService;
import io.locngo.learning.microservices.api.core.review.Review;
import io.locngo.learning.microservices.api.core.review.ReviewService;
import io.locngo.learning.microservices.util.exceptions.InvalidInputException;
import io.locngo.learning.microservices.util.exceptions.NotFoundException;
import io.locngo.learning.microservices.util.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final RestTemplate restTemplate;

    private final ObjectMapper mapper;

    private final String productServiceUrl;

    private final String recommendationServiceUrl;

    private final String reviewServiceUrl;

    public ProductCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int productServicePort,
            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") int recommendationPort,
            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.review-service.port}") int reviewServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;


        this.productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/products";
        this.recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationPort + "/recommendations";
        this.reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/reviews";
    }

    @Override
    public Product createProduct(Product body) {
        try {
            String url = this.productServiceUrl;
            LOGGER.debug("Will post a new product to URL: {}", url);

            Product product = this.restTemplate.postForObject(url, body, Product.class);
            LOGGER.debug("Created a product with id: {}", product.getProductId());

            return product;
        } catch (HttpClientErrorException exception) {
            throw handleHttpClientException(exception);
        }
    }

    @Override
    public Product getProduct(int productId) {
        try {
            String url = this.productServiceUrl + "/" + productId;
            LOGGER.debug("Will call getProduct API on URL: {}", url);

            Product product = restTemplate.getForObject(url, Product.class);
            LOGGER.debug("Found a product with id: {}", productId);

            return product;
        } catch (HttpClientErrorException exception) {
            throw handleHttpClientException(exception);
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {
            String url = this.productServiceUrl + "/" + productId;
            LOGGER.debug("Will call the deleteProduct API on URL: {}", url);

            this.restTemplate.delete(url);
        } catch (HttpClientErrorException exception) {
            throw handleHttpClientException(exception);
        }
    }

    @Override
    public Recommendation createRecommendation(Recommendation body) {
        try {
            String url = recommendationServiceUrl;
            LOGGER.debug("Will post a new recommendation to URL: {}", url);

            Recommendation recommendation = this.restTemplate.postForObject(url, body, Recommendation.class);
            LOGGER.debug("Created a recommendation with product id: {}", recommendation.getProductId());

            return recommendation;

        } catch (HttpClientErrorException exception) {
            throw handleHttpClientException(exception);
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        try {
            String url = this.recommendationServiceUrl + "?productId=" + productId;

            LOGGER.debug("Will call getRecommendations API on URL: {}", url);
            List<Recommendation> recommendations = this.restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Recommendation>>() {
            }).getBody();

            LOGGER.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
            return recommendations;
        } catch (Exception exception) {
            LOGGER.warn("Got an exception while requesting recommendations, return zero recommendations: {}", exception.getMessage());
            return new LinkedList<>();
        }
    }

    @Override
    public void deleteRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl + "?productId=" + productId;
            LOGGER.debug("Will call deleteRecommendations API on URL: {}", url);

            restTemplate.delete(url);
        } catch (HttpClientErrorException exception) {
            throw handleHttpClientException(exception);
        }
    }

    @Override
    public Review createReview(Review body) {
        try {
            String url = reviewServiceUrl;
            LOGGER.debug("Will post a new review to URL: {}", url);

            Review review = restTemplate.postForObject(url, body, Review.class);
            LOGGER.debug("Created a review with product id: {}", review.getProductId());
            return review;
        } catch (HttpClientErrorException exception) {
            throw handleHttpClientException(exception);
        }
    }

    @Override
    public List<Review> getReviews(int productId) {
        try {
            String url = this.reviewServiceUrl + "?productId=" + productId;

            LOGGER.debug("Will call getReviews API on URL: {}", url);
            List<Review> reviews = this.restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {
            }).getBody();

            LOGGER.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
            return reviews;
        } catch (Exception exception) {
            LOGGER.warn("Got an exception while requesting reviews, return zero reviews: {}", exception.getMessage());
            return new LinkedList<>();
        }
    }

    @Override
    public void deleteReviews(int productId) {
        try {
            String url = reviewServiceUrl + "?productId=" + productId;
            LOGGER.debug("Will call the deleteReviews API on URL: {}", url);

            restTemplate.delete(url);
        } catch (HttpClientErrorException exception) {
            throw handleHttpClientException(exception);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException exception) {
        switch (exception.getStatusCode()) {
            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(exception));
            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(getErrorMessage(exception));
            default:
                LOGGER.warn("Got a unexpected HTTP error: {}, will rethrow it", exception.getStatusCode());
                LOGGER.warn("Error body: {}", exception.getResponseBodyAsString());
                return exception;
        }
    }

    private String getErrorMessage(HttpClientErrorException exception) {
        try {
            return mapper.readValue(exception.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException e) {
            return e.getMessage();
        }
    }
}
