package org.sixpang.productservice.application;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.commonserver.security.UserPrincipal;
import org.sixpang.productservice.application.dto.*;
import org.sixpang.productservice.domain.model.Inventory;
import org.sixpang.productservice.domain.model.Product;
import org.sixpang.productservice.domain.repository.InventoryRepository;
import org.sixpang.productservice.domain.repository.ProductRepository;
import org.sixpang.productservice.infrastructure.exception.ProductErrorCode;
import org.springframework.cloud.openfeign.EnableFeignClients;
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
    private final UserClient userClient;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request, UserPrincipal user) {
        UserPermissionInfo userInfo = getUserPermissionInfo(user);

        validateCreatePermission(userInfo, request.companyId(), request.hubId());

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
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request, UserPrincipal user) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

        UserPermissionInfo userInfo = getUserPermissionInfo(user);

        validateUpdatePermission(userInfo, product);

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
    public void deleteProduct(UUID productId,UserPrincipal user) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));


        UserPermissionInfo userInfo = getUserPermissionInfo(user);

        validateDeletePermission(userInfo, product);

        product.delete();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID productId,UserPrincipal user) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

        UserPermissionInfo userInfo = getUserPermissionInfo(user);

        validateReadPermission(userInfo, product);

        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProducts(ProductSearchRequest request, Pageable pageable, UserPrincipal user) {
        UserPermissionInfo userInfo = getUserPermissionInfo(user);

        validateSearchPermission(userInfo);

        Page<Product> page = productRepository.searchProducts(request, pageable);

        return PageResponse.from(
                page.map(ProductResponse::from)
        );
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(UUID productId, UserPrincipal user) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

        UserPermissionInfo userInfo = getUserPermissionInfo(user);

        validateReadPermission(userInfo, product);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.INVENTORY_NOT_FOUND));

        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse increaseInventory(UUID productId, Long amount,UserPrincipal user) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

        UserPermissionInfo userInfo = getUserPermissionInfo(user);

        validateUpdatePermission(userInfo, product);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.INVENTORY_NOT_FOUND));

        inventory.increase(amount);

        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse decreaseInventory(UUID productId, Long amount,UserPrincipal user) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

        UserPermissionInfo userInfo = getUserPermissionInfo(user);

        validateUpdatePermission(userInfo, product);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.INVENTORY_NOT_FOUND));

        inventory.decrease(amount);

        return InventoryResponse.from(inventory);
    }

    private UserPermissionInfo getUserPermissionInfo(UserPrincipal user) {
        if (user == null || user.getUserId() == null) {
            throw new CustomException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
        }

        return userClient.getUserPermissionInfo(user.getUserId());
    }

    private void validateCreatePermission(UserPermissionInfo userInfo, UUID targetCompanyId, UUID targetHubId) {
        String role = userInfo.role();

        if ("MASTER".equals(role)) {
            return;
        }

        if ("HUB_MANAGER".equals(role)) {
            if (userInfo.hubId() != null && userInfo.hubId().equals(targetHubId)) {
                return;
            }
        }

        if ("COMPANY_MANAGER".equals(role)) {
            if (userInfo.companyId() != null && userInfo.companyId().equals(targetCompanyId)) {
                return;
            }
        }

        throw new CustomException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
    }

    private void validateUpdatePermission(UserPermissionInfo userInfo, Product product) {
        String role = userInfo.role();

        if ("MASTER".equals(role)) {
            return;
        }

        if ("HUB_MANAGER".equals(role)) {
            if (userInfo.hubId() != null && userInfo.hubId().equals(product.getHubId())) {
                return;
            }
            throw new CustomException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
        }

        if ("COMPANY_MANAGER".equals(role)) {
            if (userInfo.companyId() != null && userInfo.companyId().equals(product.getCompanyId())) {
                return;
            }
        }

        throw new CustomException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
    }

    private void validateDeletePermission(UserPermissionInfo userInfo, Product product) {
        String role = userInfo.role();

        if ("MASTER".equals(role)) {
            return;
        }

        if ("HUB_MANAGER".equals(role)) {
            if (userInfo.hubId() != null && userInfo.hubId().equals(product.getHubId())) {
                return;
            }
        }

        throw new CustomException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
    }

    private void validateReadPermission(UserPermissionInfo userInfo, Product product) {
        String role = userInfo.role();

        if ("MASTER".equals(role)) {
            return;
        }

        if ("HUB_MANAGER".equals(role)) {
            if (userInfo.hubId() != null && userInfo.hubId().equals(product.getHubId())) {
                return;
            }
        }

        if ("COMPANY_MANAGER".equals(role)) {
            if (userInfo.companyId() != null && userInfo.companyId().equals(product.getCompanyId())) {
                return;
            }
        }

        if ("DRIVER_MANAGER".equals(role)) {
            return;
        }

        throw new CustomException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
    }

    private void validateSearchPermission(UserPermissionInfo userInfo) {
        String role = userInfo.role();

        if ("MASTER".equals(role)
                || "HUB_MANAGER".equals(role)
                || "COMPANY_MANAGER".equals(role)
                || "DRIVER_MANAGER".equals(role)) {
            return;
        }

        throw new CustomException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
    }

}
