package org.sixpang.productservice.application;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.productservice.application.dto.*;
import org.sixpang.productservice.domain.model.Inventory;
import org.sixpang.productservice.domain.model.Product;
import org.sixpang.productservice.domain.repository.InventoryRepository;
import org.sixpang.productservice.domain.repository.ProductRepository;
import org.sixpang.productservice.infrastructure.exception.ProductErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {

        if (productRepository.existsByNameAndDeletedAtIsNull(request.name())) {
            throw new CustomException(ProductErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        Product product = new Product(
                request.name(),
                request.price(),
                request.companyId(),
                request.hubId()
        );

        Product savedProduct = productRepository.save(product);

        Inventory inventory = new Inventory(savedProduct.getId());
        inventoryRepository.save(inventory);

        return ProductResponse.from(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getName().equals(request.name())
                && productRepository.existsByNameAndDeletedAtIsNull(request.name())) {
            throw new CustomException(ProductErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        product.update(
                request.name(),
                request.price(),
                request.companyId(),
                request.hubId()
        );

        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

        product.delete();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID productId) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProducts(ProductSearchRequest request, Pageable pageable) {
        Page<Product> page = productRepository.searchProducts(request, pageable);

        return PageResponse.from(
                page.map(ProductResponse::from)
        );
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(UUID productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.INVENTORY_NOT_FOUND));

        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse increaseInventory(UUID productId, Long amount) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.INVENTORY_NOT_FOUND));

        inventory.increase(amount);

        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse decreaseInventory(UUID productId, Long amount) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.INVENTORY_NOT_FOUND));

        inventory.decrease(amount);

        return InventoryResponse.from(inventory);
    }


}
