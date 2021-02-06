package io.locngo.learning.microservices.api.core.review;

import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ReviewService {

    @PostMapping(
            value = "/reviews",
            consumes = "application/json",
            produces = "application/json"
    )
    Review createReview(@RequestBody Review body);

    @GetMapping(
            value = "/reviews",
            produces = "application/json"
    )
    List<Review> getReviews(@RequestParam(value = "productId") int productId);

    @DeleteMapping(value = "/reviews")
    void deleteReviews(@RequestParam(value = "productId") int productId);
}
