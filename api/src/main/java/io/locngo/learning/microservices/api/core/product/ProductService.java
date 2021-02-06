package io.locngo.learning.microservices.api.core.product;

import org.springframework.web.bind.annotation.*;

public interface ProductService {

    @PostMapping(
            value = "/products",
            consumes = "application/json",
            produces = "application/json"
    )
    Product createProduct(@RequestBody Product body);

    @GetMapping(
            value = "/products/{productId}",
            produces = "application/json"
    )
    Product getProduct(@PathVariable(name = "productId") int productId);

    @DeleteMapping(
            value = "/products/{productId}"
    )
    void deleteProduct(@PathVariable int productId);
}
