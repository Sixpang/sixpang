package org.sixpang.hubservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.hubservice.application.dto.HubRequestDto;
import org.sixpang.hubservice.application.dto.HubResponseDto;
import org.sixpang.hubservice.domain.repository.HubRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubService {
    private final HubRepository hubRepository;

    @Transactional
    public HubResponseDto register(HubRequestDto requestDto){

    }
}
