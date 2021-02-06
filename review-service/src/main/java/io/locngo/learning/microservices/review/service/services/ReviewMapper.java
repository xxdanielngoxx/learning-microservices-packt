package io.locngo.learning.microservices.review.service.services;

import io.locngo.learning.microservices.api.core.review.Review;
import io.locngo.learning.microservices.review.service.persistence.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Review entityToApi(ReviewEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    ReviewEntity apiToEntity(Review review);

    List<Review> entityListToApiList(List<ReviewEntity> entities);
    List<ReviewEntity> apiListToEntityList(List<Review> reviews);
}
