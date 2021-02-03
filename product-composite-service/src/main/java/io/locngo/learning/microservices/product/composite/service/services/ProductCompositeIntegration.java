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


        this.productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/products/";
        this.recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationPort + "/recommendations?productId=";
        this.reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/reviews?productId=";
    }

    @Override
    public Product getProduct(int productId) {
        try {
            String url = this.productServiceUrl + productId;
            LOGGER.debug("Will call getProduct API on URL: {}", url);

            Product product = restTemplate.getForObject(url, Product.class);
            LOGGER.debug("Found a product with id: {}", productId);

            return product;
        } catch (HttpClientErrorException exception) {
            switch (exception.getStatusCode()) {
                case NOT_FOUND:
                    throw new NotFoundException(this.getErrorMessage(exception));
                case UNPROCESSABLE_ENTITY:
                    throw new InvalidInputException(this.getErrorMessage(exception));
                default:
                    LOGGER.warn("Got a unexpected HTTP error: {}, will rethrow it", exception.getStatusCode());
                    LOGGER.warn("Error body: {}", exception.getResponseBodyAsString());
                    throw exception;
            }
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        try {
            String url = this.recommendationServiceUrl + productId;

            LOGGER.debug("Will call getRecommendations API on URL: {}", url);
            List<Recommendation> recommendations = this.restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Recommendation>>() {}).getBody();

            LOGGER.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
            return recommendations;
        } catch (Exception exception) {
            LOGGER.warn("Got an exception while requesting recommendations, return zero recommendations: {}", exception.getMessage());
            return new LinkedList<>();
        }
    }

    @Override
    public List<Review> getReviews(int productId) {
        try {
            String url = this.reviewServiceUrl + productId;

            LOGGER.debug("Will call getReviews API on URL: {}", url);
            List<Review> reviews = this.restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {}).getBody();

            LOGGER.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
            return reviews;
        } catch (Exception exception) {
            LOGGER.warn("Got an exception while requesting reviews, return zero reviews: {}", exception.getMessage());
            return new LinkedList<>();
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
