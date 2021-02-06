package io.locngo.learning.microservices.product.service;

import io.locngo.learning.microservices.api.core.product.Product;
import io.locngo.learning.microservices.product.service.persistence.ProductEntity;
import io.locngo.learning.microservices.product.service.services.ProductMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.Assert.*;

public class MapperTests {
    private ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Product api = new Product(1, "n", 1, "sa");

        ProductEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getProductId(), entity.getProductId());
        assertEquals(api.getName(), entity.getName());
        assertEquals(api.getWeight(), entity.getWeight());

        Product api2 = mapper.entityToApi(entity);

        assertEquals(api2.getProductId(), entity.getProductId());
        assertEquals(api2.getName(), entity.getName());
        assertEquals(api2.getWeight(), entity.getWeight());
        assertNull(api2.getServiceAddress());
    }
}
