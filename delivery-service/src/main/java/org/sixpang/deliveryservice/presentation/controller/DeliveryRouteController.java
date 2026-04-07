package org.sixpang.deliveryservice.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.application.dto.DeliveryRouteRequest;
import org.sixpang.deliveryservice.application.dto.DeliveryRouteResponse;
import org.sixpang.deliveryservice.application.service.service.DeliveryRouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries/routes")
@RequiredArgsConstructor
public class DeliveryRouteController {
    private final DeliveryRouteService deliveryRouteService;

    @PostMapping
    public ResponseEntity<UUID> createRoute(@RequestBody DeliveryRouteRequest request) {
        UUID routeId = deliveryRouteService.createDeliveryRoute(
                request.deliveryId(),
                request.hubSequence(),
                request.departureHub(),
                request.arrivalHub(),
                request.hubDeliveryManagerId()
        );
        return ResponseEntity.ok(routeId);
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<List<DeliveryRouteResponse>> getRoutesByDelivery(@PathVariable UUID deliveryId) {
        List<DeliveryRouteResponse> routes = deliveryRouteService.getRoutesByDelivery(deliveryId);
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<DeliveryRouteResponse> getRouteDetail(@PathVariable UUID routeId) {
        DeliveryRouteResponse response = deliveryRouteService.getRouteDetail(routeId);
        return ResponseEntity.ok(response);
    }
}