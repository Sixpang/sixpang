package org.sixpang.productservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.commonserver.security.UserPrincipal;
import org.sixpang.productservice.application.ProductService;
import org.sixpang.productservice.application.dto.*;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "상품", description = "상품 및 재고 관련 API")
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "상품 등록",
            description = """
                    상품을 등록합니다.
                    마스터 관리자 또는 허브 관리자만 등록할 수 있습니다.
                    등록 시 상품에 대한 재고 엔티티도 함께 생성됩니다.
                    """
    )

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestBody CreateProductRequest request,
            @AuthenticationPrincipal UserPrincipal user){
        ProductResponse response = productService.createProduct(request,user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED, "상품 등록에 성공했습니다.",response));
    }

    @Operation(
            summary = "상품 수정",
            description = """
                    상품 정보를 수정합니다.
                    마스터 관리자 또는 해당 허브 관리자가 수정할 수 있습니다.
                    """
    )

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

    @Operation(
            summary = "상품 삭제",
            description = """
                    상품을 삭제합니다. (논리 삭제)
                    마스터 관리자 또는 해당 허브 관리자가 삭제할 수 있습니다.
                    """
    )

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

    @Operation(
            summary = "상품 상세 조회",
            description = "특정 상품의 상세 정보를 조회합니다."
    )

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

    @Operation(
            summary = "상품 목록 조회",
            description = """
                    상품 목록을 조회합니다.
                    상품명, 가격, 업체ID, 허브ID 조건으로 검색이 가능합니다.
                    """
    )

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProducts(
            @ParameterObject ProductSearchRequest request,
            @ParameterObject Pageable pageable,
            @AuthenticationPrincipal UserPrincipal user
    ){
        PageResponse<ProductResponse> response = productService.getProducts(request, pageable,user);

        return ResponseEntity.ok(
                ApiResponse.of("상품 목록 조회에 성공했습니다.", response)
        );
    }

    @Operation(
            summary = "재고 조회",
            description = "특정 상품의 재고 정보를 조회합니다."
    )

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

    @Operation(
            summary = "재고 증가",
            description = """
                    특정 상품의 재고를 증가시킵니다.
                    입고 또는 재고 보충 상황에서 사용합니다.
                    """
    )

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

    @Operation(
            summary = "재고 감소",
            description = """
                    특정 상품의 재고를 감소시킵니다.
                    출고, 주문, 차감 상황에서 사용합니다.
                    """
    )

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

