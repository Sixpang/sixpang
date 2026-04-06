package org.sixpang.productservice.presentation;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.productservice.application.ProductService;
import org.sixpang.productservice.application.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody CreateProductRequest request){
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED, "상품 등록에 성공했습니다.",response));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID productId,
            @RequestBody UpdateProductRequest request
            ){
        ProductResponse response = productService.updateProduct(productId,request);

        return ResponseEntity.ok(
                ApiResponse.of("상품 수정에 성공했습니다.", response)
        );
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID productId){
        productService.deleteProduct(productId);

        return  ResponseEntity.ok(
                ApiResponse.of("상품 삭제에 성공했습니다.", null)
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable UUID productId){
        ProductResponse response = productService.getProduct(productId);
        return ResponseEntity.ok(
                ApiResponse.of("상품 조회에 성공했습니다.", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProducts(
            ProductSearchRequest request,
            Pageable pageable
    ){
        PageResponse<ProductResponse> response = productService.getProducts(request, pageable);

        return ResponseEntity.ok(
                ApiResponse.of("상품 목록 조회에 성공했습니다.", response)
        );
    }

    @GetMapping("/{productId}/inventory")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(@PathVariable UUID productId) {
        InventoryResponse response = productService.getInventory(productId);

        return ResponseEntity.ok(
                ApiResponse.of("재고 조회에 성공했습니다.", response)
        );
    }

    @PatchMapping("/{productId}/inventory/increase")
    public ResponseEntity<ApiResponse<InventoryResponse>> increaseInventory(
            @PathVariable UUID productId,
            @RequestBody UpdateInventoryRequest request
    ) {
        InventoryResponse response = productService.increaseInventory(productId, request.amount());

        return ResponseEntity.ok(
                ApiResponse.of("재고 증가에 성공했습니다.", response)
        );
    }

    @PatchMapping("/{productId}/inventory/decrease")
    public ResponseEntity<ApiResponse<InventoryResponse>> decreaseInventory(
            @PathVariable UUID productId,
            @RequestBody UpdateInventoryRequest request
    ) {
        InventoryResponse response = productService.decreaseInventory(productId, request.amount());

        return ResponseEntity.ok(
                ApiResponse.of("재고 감소에 성공했습니다.", response)
        );
    }
}

