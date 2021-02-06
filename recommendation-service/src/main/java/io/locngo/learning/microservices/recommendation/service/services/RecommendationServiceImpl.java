package io.locngo.learning.microservices.recommendation.service.services;

import io.locngo.learning.microservices.api.core.recommendation.Recommendation;
import io.locngo.learning.microservices.api.core.recommendation.RecommendationService;
import io.locngo.learning.microservices.recommendation.service.persistence.RecommendationEntity;
import io.locngo.learning.microservices.recommendation.service.persistence.RecommendationRepository;
import io.locngo.learning.microservices.util.exceptions.InvalidInputException;
import io.locngo.learning.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final RecommendationRepository recommendationRepository;

    private final RecommendationMapper recommendationMapper;

    private final ServiceUtil serviceUtil;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository, RecommendationMapper recommendationMapper, ServiceUtil serviceUtil) {
        this.recommendationRepository = recommendationRepository;
        this.recommendationMapper = recommendationMapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Recommendation createRecommendation(Recommendation body) {
        try {
            RecommendationEntity entity = recommendationMapper.apiToEntity(body);
            RecommendationEntity newEntity = recommendationRepository.save(entity);

            LOGGER.debug("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(), body.getRecommendationId());
            return recommendationMapper.entityToApi(newEntity);
        } catch (DuplicateKeyException exception) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id: " + body.getRecommendationId());
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        List<RecommendationEntity> entityList = recommendationRepository.findByProductId(productId);
        List<Recommendation> apiList = recommendationMapper.entityListToApiList(entityList);
        apiList.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOGGER.debug("getRecommendations: response size: {}", apiList.size());

        return apiList;
    }

    @Override
    public void deleteRecommendations(int productId) {
        LOGGER.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
        recommendationRepository.deleteAll(recommendationRepository.findByProductId(productId));
    }
}
