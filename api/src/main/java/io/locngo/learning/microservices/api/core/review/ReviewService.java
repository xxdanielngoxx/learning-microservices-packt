package io.locngo.learning.microservices.api.core.review;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ReviewService {

    @GetMapping(
            value = "/reviews",
            produces = "application/json"
    )
    List<Review> getReviews(@RequestParam(value = "productId") int productId);
}
