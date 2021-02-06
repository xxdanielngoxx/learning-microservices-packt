package io.locngo.learning.microservices.review.service.services;

import io.locngo.learning.microservices.api.core.review.Review;
import io.locngo.learning.microservices.api.core.review.ReviewService;
import io.locngo.learning.microservices.review.service.persistence.ReviewEntity;
import io.locngo.learning.microservices.review.service.persistence.ReviewRepository;
import io.locngo.learning.microservices.util.exceptions.InvalidInputException;
import io.locngo.learning.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final ReviewMapper mapper;

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ServiceUtil serviceUtil, ReviewMapper mapper, ReviewRepository reviewRepository) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review createReview(Review body) {
        try {
            ReviewEntity entity = mapper.apiToEntity(body);
            ReviewEntity newEntity = reviewRepository.save(entity);

            LOGGER.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
            return mapper.entityToApi(newEntity);
        } catch (DataIntegrityViolationException exception) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id: " + body.getReviewId());
        }
    }

    @Override
    public List<Review> getReviews(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        List<ReviewEntity> entityList = reviewRepository.findByProductId(productId);
        List<Review> reviewList = mapper.entityListToApiList(entityList);
        reviewList.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOGGER.debug("getReviews: response size: {}", reviewList.size());
        return reviewList;
    }

    @Override
    public void deleteReviews(int productId) {
        LOGGER.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        reviewRepository.deleteAll(reviewRepository.findByProductId(productId));
    }
}
