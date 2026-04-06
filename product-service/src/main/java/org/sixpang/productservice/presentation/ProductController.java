package org.sixpang.productservice.presentation;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.commonserver.security.UserPrincipal;
import org.sixpang.productservice.application.ProductService;
import org.sixpang.productservice.application.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestBody CreateProductRequest request,
            @AuthenticationPrincipal UserPrincipal user){
        ProductResponse response = productService.createProduct(request,user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED, "상품 등록에 성공했습니다.",response));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID productId,
            @RequestBody UpdateProductRequest request,
            @AuthenticationPrincipal UserPrincipal user

            ){
        ProductResponse response = productService.updateProduct(productId,request,user);

        return ResponseEntity.ok(
                ApiResponse.of("상품 수정에 성공했습니다.", response)
        );
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable UUID productId,
            @AuthenticationPrincipal UserPrincipal user
    ){
        productService.deleteProduct(productId,user);

        return  ResponseEntity.ok(
                ApiResponse.of("상품 삭제에 성공했습니다.", null)
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @PathVariable UUID productId,
            @AuthenticationPrincipal UserPrincipal user
            ){
        ProductResponse response = productService.getProduct(productId,user);
        return ResponseEntity.ok(
                ApiResponse.of("상품 조회에 성공했습니다.", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProducts(
            ProductSearchRequest request,
            Pageable pageable,
            @AuthenticationPrincipal UserPrincipal user
    ){
        PageResponse<ProductResponse> response = productService.getProducts(request, pageable,user);

        return ResponseEntity.ok(
                ApiResponse.of("상품 목록 조회에 성공했습니다.", response)
        );
    }

    @GetMapping("/{productId}/inventory")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(
            @PathVariable UUID productId,
            @AuthenticationPrincipal UserPrincipal user
            ) {
        InventoryResponse response = productService.getInventory(productId,user);

        return ResponseEntity.ok(
                ApiResponse.of("재고 조회에 성공했습니다.", response)
        );
    }

    @PatchMapping("/{productId}/inventory/increase")
    public ResponseEntity<ApiResponse<InventoryResponse>> increaseInventory(
            @PathVariable UUID productId,
            @RequestBody UpdateInventoryRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        InventoryResponse response = productService.increaseInventory(productId, request.amount(),user);

        return ResponseEntity.ok(
                ApiResponse.of("재고 증가에 성공했습니다.", response)
        );
    }

    @PatchMapping("/{productId}/inventory/decrease")
    public ResponseEntity<ApiResponse<InventoryResponse>> decreaseInventory(
            @PathVariable UUID productId,
            @RequestBody UpdateInventoryRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        InventoryResponse response = productService.decreaseInventory(productId, request.amount(),user);

        return ResponseEntity.ok(
                ApiResponse.of("재고 감소에 성공했습니다.", response)
        );
    }
}

