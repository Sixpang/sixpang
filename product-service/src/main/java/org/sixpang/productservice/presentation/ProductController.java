package org.sixpang.productservice.presentation;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.productservice.application.ProductService;
import org.sixpang.productservice.application.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request){
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID productId,
            @RequestBody UpdateProductRequest request
            ){
        return ResponseEntity.ok(productService.updateProduct(productId,request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId){
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID productId){
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getProducts(
            ProductSearchRequest request,
            Pageable pageable
    ){
        return ResponseEntity.ok(productService.getProducts(request, pageable));
    }

    @GetMapping("/{productId}/inventory")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getInventory(productId));
    }

    @PatchMapping("/{productId}/inventory/increase")
    public ResponseEntity<InventoryResponse> increaseInventory(
            @PathVariable UUID productId,
            @RequestBody UpdateInventoryRequest request
    ) {
        return ResponseEntity.ok(productService.increaseInventory(productId, request.amount()));
    }

    @PatchMapping("/{productId}/inventory/decrease")
    public ResponseEntity<InventoryResponse> decreaseInventory(
            @PathVariable UUID productId,
            @RequestBody UpdateInventoryRequest request
    ) {
        return ResponseEntity.ok(productService.decreaseInventory(productId, request.amount()));
    }
}

