package io.locngo.learning.microservices.api.core.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ProductService {

    @GetMapping(
            value = "/products/{productId}",
            produces = "application/json"
    )
    Product getProduct(@PathVariable(name = "productId") int productId);
}
