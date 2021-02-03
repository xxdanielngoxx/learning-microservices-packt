package io.locngo.learning.microservices.product.service.services;

import io.locngo.learning.microservices.api.core.product.Product;
import io.locngo.learning.microservices.api.core.product.ProductService;
import io.locngo.learning.microservices.util.exceptions.InvalidInputException;
import io.locngo.learning.microservices.util.exceptions.NotFoundException;
import io.locngo.learning.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ServiceUtil serviceUtil;

    public ProductServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Product getProduct(int productId) {
        LOGGER.debug("/product return the found product for productId={}", productId);

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        if (productId == 13) throw new NotFoundException("No product found for productId: " + productId);

        return new Product(productId, "name-" + productId, 123, serviceUtil.getServiceAddress());
    }
}
