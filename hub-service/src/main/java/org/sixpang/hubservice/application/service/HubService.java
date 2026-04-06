package org.sixpang.hubservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.hubservice.application.dto.HubRequestDto;
import org.sixpang.hubservice.application.dto.HubResponseDto;
import org.sixpang.hubservice.domain.model.entity.Hub;
import org.sixpang.hubservice.domain.repository.HubRepository;
import org.sixpang.hubservice.exception.HubErrorCode;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubService {
    private final HubRepository hubRepository;
    private final RouteService routeService;

    // 허브 등록
    @Transactional
    @CacheEvict(cacheNames = {"hub", "hubList"}, allEntries = true)
    public HubResponseDto register(UUID userId, HubRequestDto requestDto){
        if (hubRepository.existsByNameAndDeletedAtIsNull(requestDto.getName())) {
            throw new CustomException(HubErrorCode.EXISTS_HUB);
        }

        Hub hub = requestDto.toEntity();

        hubRepository.save(hub);

        routeService.generateRoutesForNewHub(hub.getId());

        return HubResponseDto.from(hub);
    }

    // 허브 정보 단일 조회
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "hub", key = "#id")
    public HubResponseDto getHubInfo(UUID id){
        Hub hub = findHub(id);
        return HubResponseDto.from(hub);
    }

    // 허브 정보 목록 조회
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "hubList", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public PageResponse<HubResponseDto> getAllHubInfo(Pageable pageable){
        Page<Hub> page = hubRepository.findAllByDeletedAtIsNull(pageable);

        return PageResponse.from(page.map(HubResponseDto::from));
    }

    // 허브 정보 수정
    @Transactional
    @CacheEvict(cacheNames = {"hub", "hubList"}, allEntries = true)
    public HubResponseDto updateHubInfo(UUID id, UUID userId, HubRequestDto requestDto) {
        Hub hub = findHub(id);

        validateDuplicatedHub(hub, requestDto);

        hub.updateInfo(requestDto.getName(), requestDto.getAddress(), requestDto.getLatitude(),
                requestDto.getLongitude(), requestDto.getStatus());

        return HubResponseDto.from(hub);
    }

    // 허브 삭제
    @Transactional
    @CacheEvict(cacheNames = {"hub", "hubList"}, allEntries = true)
    public void deleteHub(UUID id, UUID userId) {
        Hub hub = findHub(id);

        hub.delete(userId);

        routeService.disableRoutesForHub(hub.getId(), userId);
    }

    // ID로 허브 찾기
    private Hub findHub(UUID id) {
        return hubRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(HubErrorCode.HUB_NOT_FOUND));
    }

    // 허브 중복 여부 검사
    private void validateDuplicatedHub(Hub hub, HubRequestDto requestDto){
        if(!hub.getName().equals(requestDto.getName())
                && hubRepository.existsByNameAndDeletedAtIsNull(requestDto.getName())){
            throw new CustomException(HubErrorCode.EXISTS_HUB);
        }
    }

    // 허브 존재 여부 확인
    public boolean exists(UUID id) {
        return hubRepository.findByIdAndDeletedAtIsNull(id).isPresent();
    }
}
