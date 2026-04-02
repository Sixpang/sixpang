package org.sixpang.deliveryservice.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.application.DeliveryManagerCreateRequest;
import org.sixpang.deliveryservice.application.DeliveryManagerResponse;
import org.sixpang.deliveryservice.application.DeliveryManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries/managers")
@RequiredArgsConstructor
public class DeliveryManagerController {

    private final DeliveryManagerService deliveryManagerService;

    @PostMapping
    public ResponseEntity<DeliveryManagerResponse> create(
            @RequestBody @Valid DeliveryManagerCreateRequest request
            //수정예약:회원 인증/인가 완성후 수정
            ){
        String role = "MASTER";
        UUID requestUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryManagerService.create(request, role, requestUserId));
    }

}
