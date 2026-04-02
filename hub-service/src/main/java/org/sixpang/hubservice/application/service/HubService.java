package org.sixpang.hubservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.global.ErrorCode;
import org.sixpang.hubservice.application.dto.HubRequestDto;
import org.sixpang.hubservice.application.dto.HubResponseDto;
import org.sixpang.hubservice.domain.model.entity.Hub;
import org.sixpang.hubservice.domain.repository.HubRepository;
import org.sixpang.userservice.domain.model.entity.User;
import org.sixpang.userservice.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubService {
    private final HubRepository hubRepository;
    private final UserRepository userRepository;

    // 허브 등록
    @Transactional
    public HubResponseDto register(HubRequestDto requestDto, Long userId){
        User user = userRepository.findByIdAndDeletedAtIsNull(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if (hubRepository.existsByNameAndDeletedAtIsNull(requestDto.getName())) {
            throw new CustomException(ErrorCode.EXISTS_HUB);
        }

        Hub hub = Hub.of(
                requestDto.getName(),
                requestDto.getAddress(),
                requestDto.getLatitude(),
                requestDto.getLongitude(),
                requestDto.getStatus()
        );

        hubRepository.save(hub);

        return HubResponseDto.from(hub);
    }

    // 허브 정보 단일 조회
    @Transactional(readOnly = true)
    public HubResponseDto getHubInfo(UUID id){
        Hub hub = findHub(id);
        return HubResponseDto.from(hub);
    }

    // 허브 정보 수정
    @Transactional
    public HubResponseDto updateHubInfo(UUID id, HubRequestDto requestDto) {
        Hub hub = findHub(id);

        validateDuplicatedHub(hub, requestDto);

        hub.updateInfo(requestDto.getName(), requestDto.getAddress(), requestDto.getLatitude(),
                requestDto.getLatitude(), requestDto.getStatus());

        return HubResponseDto.from(hub);
    }

    // 허브 삭제
    @Transactional
    public void deleteHub(UUID id) {
        Hub hub = hubRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new CustomException(ErrorCode.HUB_NOT_FOUND)
        );

        hub.delete(id);
    }

    // ID로 허브 찾기
    private Hub findHub(UUID id) {
        return hubRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.HUB_NOT_FOUND));
    }

    // 허브 중복 여부 검사
    private void validateDuplicatedHub(Hub hub, HubRequestDto requestDto){
        if(!hub.getName().equals(requestDto.getName())
                && hubRepository.existsByNameAndDeletedAtIsNull(requestDto.getName())){
            throw new CustomException(ErrorCode.EXISTS_HUB);
        }
    }
}
