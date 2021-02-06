package io.locngo.learning.microservices.recommendation.service.services;

import io.locngo.learning.microservices.api.core.recommendation.Recommendation;
import io.locngo.learning.microservices.recommendation.service.persistence.RecommendationEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-06T11:40:50+0700",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class RecommendationMapperImpl implements RecommendationMapper {

    @Override
    public Recommendation entityToApi(RecommendationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Recommendation recommendation = new Recommendation();

        recommendation.setRate( entity.getRating() );
        recommendation.setProductId( entity.getProductId() );
        recommendation.setRecommendationId( entity.getRecommendationId() );
        recommendation.setAuthor( entity.getAuthor() );
        recommendation.setContent( entity.getContent() );

        return recommendation;
    }

    @Override
    public RecommendationEntity apiToEntity(Recommendation recommendation) {
        if ( recommendation == null ) {
            return null;
        }

        RecommendationEntity recommendationEntity = new RecommendationEntity();

        recommendationEntity.setRating( recommendation.getRate() );
        recommendationEntity.setProductId( recommendation.getProductId() );
        recommendationEntity.setRecommendationId( recommendation.getRecommendationId() );
        recommendationEntity.setAuthor( recommendation.getAuthor() );
        recommendationEntity.setContent( recommendation.getContent() );

        return recommendationEntity;
    }

    @Override
    public List<Recommendation> entityListToApiList(List<RecommendationEntity> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<Recommendation> list = new ArrayList<Recommendation>( entityList.size() );
        for ( RecommendationEntity recommendationEntity : entityList ) {
            list.add( entityToApi( recommendationEntity ) );
        }

        return list;
    }

    @Override
    public List<RecommendationEntity> apiListToEntityList(List<Recommendation> recommendationList) {
        if ( recommendationList == null ) {
            return null;
        }

        List<RecommendationEntity> list = new ArrayList<RecommendationEntity>( recommendationList.size() );
        for ( Recommendation recommendation : recommendationList ) {
            list.add( apiToEntity( recommendation ) );
        }

        return list;
    }
}
