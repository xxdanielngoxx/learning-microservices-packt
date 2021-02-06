package io.locngo.learning.microservices.recommendation.service.services;

import io.locngo.learning.microservices.api.core.recommendation.Recommendation;
import io.locngo.learning.microservices.recommendation.service.persistence.RecommendationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mappings({
            @Mapping(target = "rate", source = "entity.rating"),
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Recommendation entityToApi(RecommendationEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "rating", source = "recommendation.rate")
    })
    RecommendationEntity apiToEntity(Recommendation recommendation);

    List<Recommendation> entityListToApiList(List<RecommendationEntity> entityList);
    List<RecommendationEntity> apiListToEntityList(List<Recommendation> recommendationList);
}
