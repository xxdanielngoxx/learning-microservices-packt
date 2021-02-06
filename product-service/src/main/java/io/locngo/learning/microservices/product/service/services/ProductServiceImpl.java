package io.locngo.learning.microservices.product.service.services;

import com.mongodb.DuplicateKeyException;
import io.locngo.learning.microservices.api.core.product.Product;
import io.locngo.learning.microservices.api.core.product.ProductService;
import io.locngo.learning.microservices.product.service.persistence.ProductEntity;
import io.locngo.learning.microservices.product.service.persistence.ProductRepository;
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

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public ProductServiceImpl(ServiceUtil serviceUtil, ProductRepository productRepository, ProductMapper productMapper) {
        this.serviceUtil = serviceUtil;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Product createProduct(Product body) {
        try {
            ProductEntity entity = this.productMapper.apiToEntity(body);
            ProductEntity newEntity = this.productRepository.save(entity);

            LOGGER.debug("createProduct: entity created for productId: {}", body.getProductId());
            return this.productMapper.entityToApi(newEntity);
        } catch (DuplicateKeyException exception) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId());
        }
    }

    @Override
    public Product getProduct(int productId) {
        LOGGER.debug("/product return the found product for productId={}", productId);

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        ProductEntity entity = this.productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("No product found for productId: " + productId));

        Product response = this.productMapper.entityToApi(entity);
        response.setServiceAddress(this.serviceUtil.getServiceAddress());

        LOGGER.debug("getProduct: found productId: {}", response.getProductId());

        return response;
    }

    @Override
    public void deleteProduct(int productId) {
        LOGGER.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        this.productRepository.findByProductId(productId).ifPresent(this.productRepository::delete);
    }
}
