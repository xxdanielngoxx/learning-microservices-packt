package io.locngo.learning.microservices.api.core.recommendation;

import org.springframework.web.bind.annotation.*;

import java.util.List;


public interface RecommendationService {

    @PostMapping(
            value = "/recommendations",
            produces = "application/json",
            consumes = "application/json"
    )
    Recommendation createRecommendation(@RequestBody Recommendation body);

    @GetMapping(
            value = "/recommendations",
            produces = "application/json"
    )
    List<Recommendation> getRecommendations(@RequestParam(value = "productId") int productId);

    @DeleteMapping(value = "/recommendations")
    void deleteRecommendations(@RequestParam(value = "productId") int productId);
}
