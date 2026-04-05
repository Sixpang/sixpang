package org.sixpang.hubservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.hubservice.domain.repository.RouteRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;
}
