package org.sixpang.notificationservice.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.sixpang.notificationservice.application.dto.NotificationRequestDto;
import org.sixpang.notificationservice.application.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Controller
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 알림 발송 API
     * 모든 로그인한 사용자 및 내부 시스템에서 호출 가능
     */
    @PostMapping
    public ResponseEntity<String> sendNotification(
            @RequestBody NotificationRequestDto request,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "SYSTEM") String senderId
    ) {
        // MSA 환경에서는 게이트웨이에서 넘겨준 X-User-Id 헤더를 사용하거나
        // SecurityContextHolder에서 유저 정보를 꺼내옵니다.
        notificationService.sendNotification(request, senderId);

        return ResponseEntity.ok("Notification processed successfully");
    }
}
