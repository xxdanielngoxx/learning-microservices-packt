package io.locngo.learning.microservices.recommendation.service.services;

import io.locngo.learning.microservices.api.core.recommendation.Recommendation;
import io.locngo.learning.microservices.api.core.recommendation.RecommendationService;
import io.locngo.learning.microservices.util.exceptions.InvalidInputException;
import io.locngo.learning.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final ServiceUtil serviceUtil;

    public RecommendationServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        if (productId == 113) {
            LOGGER.debug("No recommendations found for productId: {}", productId);
            return Collections.emptyList();
        }

        List<Recommendation> recommendations = new LinkedList<>();
        recommendations.add(new Recommendation(productId, 1, "Author 1", 1, "Content 1", this.serviceUtil.getServiceAddress()));
        recommendations.add(new Recommendation(productId, 2, "Author 2", 1, "Content 2", this.serviceUtil.getServiceAddress()));
        recommendations.add(new Recommendation(productId, 3, "Author 3", 1, "Content 3", this.serviceUtil.getServiceAddress()));

        LOGGER.debug("/recommendations response size: {}", recommendations.size());

        return recommendations;
    }
}
