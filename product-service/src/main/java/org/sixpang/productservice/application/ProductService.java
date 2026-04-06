package org.sixpang.productservice.application;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.productservice.application.dto.*;
import org.sixpang.productservice.domain.model.Inventory;
import org.sixpang.productservice.domain.model.Product;
import org.sixpang.productservice.domain.repository.InventoryRepository;
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
    private final InventoryRepository inventoryRepository;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request){
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
    public PageResponse<ProductResponse> getProducts(ProductSearchRequest request, Pageable pageable){
        Page<Product> page = productRepository.searchProducts(request, pageable);

        return PageResponse.from(
                page.map(ProductResponse::from)
        );
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(UUID productId){
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("재고 정보를 찾을 수 없습니다."));

        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse increaseInventory(UUID productId, Long amount){

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("재고 정보를 찾을 수 없습니다."));

        inventory.increase(amount);

        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse decreaseInventory(UUID productId, Long amount){

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("재고 정보를 찾을 수 없습니다."));

        inventory.decrease(amount);

        return InventoryResponse.from(inventory);
    }


}
