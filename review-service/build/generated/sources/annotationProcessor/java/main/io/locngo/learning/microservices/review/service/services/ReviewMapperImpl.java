package io.locngo.learning.microservices.review.service.services;

import io.locngo.learning.microservices.api.core.review.Review;
import io.locngo.learning.microservices.review.service.persistence.ReviewEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-06T11:44:26+0700",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public Review entityToApi(ReviewEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Review review = new Review();

        review.setProductId( entity.getProductId() );
        review.setReviewId( entity.getReviewId() );
        review.setAuthor( entity.getAuthor() );
        review.setSubject( entity.getSubject() );
        review.setContent( entity.getContent() );

        return review;
    }

    @Override
    public ReviewEntity apiToEntity(Review review) {
        if ( review == null ) {
            return null;
        }

        ReviewEntity reviewEntity = new ReviewEntity();

        reviewEntity.setProductId( review.getProductId() );
        reviewEntity.setReviewId( review.getReviewId() );
        reviewEntity.setAuthor( review.getAuthor() );
        reviewEntity.setSubject( review.getSubject() );
        reviewEntity.setContent( review.getContent() );

        return reviewEntity;
    }

    @Override
    public List<Review> entityListToApiList(List<ReviewEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<Review> list = new ArrayList<Review>( entities.size() );
        for ( ReviewEntity reviewEntity : entities ) {
            list.add( entityToApi( reviewEntity ) );
        }

        return list;
    }

    @Override
    public List<ReviewEntity> apiListToEntityList(List<Review> reviews) {
        if ( reviews == null ) {
            return null;
        }

        List<ReviewEntity> list = new ArrayList<ReviewEntity>( reviews.size() );
        for ( Review review : reviews ) {
            list.add( apiToEntity( review ) );
        }

        return list;
    }
}
