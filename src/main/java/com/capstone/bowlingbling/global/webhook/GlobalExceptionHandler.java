package com.capstone.bowlingbling.global.webhook;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final DiscordWebhookService discordWebhookService;

    // DiscordWebhookService 의존성 주입
    public GlobalExceptionHandler(DiscordWebhookService discordWebhookService) {
        this.discordWebhookService = discordWebhookService;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        // 에러 메시지 구성
        String errorMessage = "Exception: " + ex.getMessage();

        // 디스코드 웹훅으로 에러 메시지 전송
        discordWebhookService.sendDiscordMessage(errorMessage);

        // 클라이언트에게 응답 보냄
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
