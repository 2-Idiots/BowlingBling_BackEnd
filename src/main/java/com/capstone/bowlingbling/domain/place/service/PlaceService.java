package com.capstone.bowlingbling.domain.place.service;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
public class PlaceService {

    private final OAuth2ClientProperties oAuth2ClientProperties;
    private final RestTemplate restTemplate;

    public PlaceService(OAuth2ClientProperties oAuth2ClientProperties, RestTemplate restTemplate) {
        this.oAuth2ClientProperties = oAuth2ClientProperties;
        this.restTemplate = restTemplate;
    }

    public String searchKeyword(String query) {
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json";

        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", query)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        String clientId = oAuth2ClientProperties.getRegistration().get("kakao").getClientId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + clientId);

        HttpEntity entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
}
