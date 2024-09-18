package com.capstone.bowlingbling.global.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DiscordWebhookService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Discord 웹훅 URL
    @Value("${discord.webhook-url}")
    private String discordWebhookUrl;

    public void sendDiscordMessage(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Discord로 보낼 JSON 형식의 메시지
        String jsonPayload = "{\"content\":\"" + content + "\"}";

        HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

        // POST 요청을 통해 Discord로 메시지 전송
        restTemplate.exchange(discordWebhookUrl, HttpMethod.POST, request, String.class);
    }
}
