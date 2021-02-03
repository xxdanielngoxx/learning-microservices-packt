package io.locngo.learning.microservices.review.service.services;

import io.locngo.learning.microservices.api.core.review.Review;
import io.locngo.learning.microservices.api.core.review.ReviewService;
import io.locngo.learning.microservices.util.exceptions.InvalidInputException;
import io.locngo.learning.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;

    public ReviewServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Review> getReviews(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        if (productId == 213) {
            LOGGER.debug("No reviews found for productId: {}", productId);
            return Collections.emptyList();
        }

        List<Review> reviews = new LinkedList<>();
        reviews.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1", this.serviceUtil.getServiceAddress()));
        reviews.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2", this.serviceUtil.getServiceAddress()));
        reviews.add(new Review(productId, 3, "Author 3", "Subject 3", "Content 3", this.serviceUtil.getServiceAddress()));

        LOGGER.debug("/reviews response size: {}", reviews.size());

        return reviews;
    }
}
