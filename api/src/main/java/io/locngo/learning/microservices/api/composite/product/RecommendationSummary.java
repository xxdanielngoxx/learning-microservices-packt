package io.locngo.learning.microservices.api.composite.product;

public class RecommendationSummary {
    private final int recommendationId;
    private final String author;
    private final int rate;

    public RecommendationSummary() {
        this.recommendationId = 0;
        this.author = null;
        this.rate = 0;
    }

    public RecommendationSummary(int recommendationId, String author, int rate) {
        this.recommendationId = recommendationId;
        this.author = author;
        this.rate = rate;
    }

    public int getRecommendationId() {
        return recommendationId;
    }

    public String getAuthor() {
        return author;
    }

    public int getRate() {
        return rate;
    }
}
