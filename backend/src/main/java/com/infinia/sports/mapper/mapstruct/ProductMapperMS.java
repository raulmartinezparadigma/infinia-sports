package com.infinia.sports.mapper.mapstruct;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.dto.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper de MapStruct para Product
 */
@Mapper(config = MapStructConfig.class)
public interface ProductMapperMS {
    
    @Mapping(target = "type", expression = "java(product.getType() != null ? product.getType().name() : null)")
    @Mapping(target = "productTypeDisplayName", expression = "java(product.getType() != null ? product.getType().getDisplayName() : null)")
    ProductDTO toDTO(Product product);
    
    List<ProductDTO> toDTOList(List<Product> products);
}
