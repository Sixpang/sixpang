package org.sixpang.productservice.application;

import lombok.RequiredArgsConstructor;
import org.sixpang.productservice.application.dto.CreateProductRequest;
import org.sixpang.productservice.application.dto.ProductResponse;
import org.sixpang.productservice.application.dto.ProductSearchRequest;
import org.sixpang.productservice.application.dto.UpdateProductRequest;
import org.sixpang.productservice.domain.model.Product;
import org.sixpang.productservice.domain.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request){
        Product product = new Product(
                request.name(),
                request.price(),
                request.companyId(),
                request.hubId()
        );

        Product savedProduct = productRepository.save(product);

        return ProductResponse.from(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId,UpdateProductRequest request){
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        product.update(
                request.name(),
                request.price(),
                request.companyId(),
                request.hubId()
        );

        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(UUID productId){
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        product.delete();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID productId){
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(ProductSearchRequest request, Pageable pageable){
        return productRepository.searchProducts(request, pageable)
                .map(ProductResponse::from);
    }
}
