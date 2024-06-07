package com.example.community_old.Communicator.Authentication;

import com.example.community_old.Communicator.CommSetting;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationCommunicator {
    private final CommSetting setting;

    public AuthDto authentication(String jwt) {
        String url = setting.getAuthenticationAddress() + "/user/authentication/get-id";

        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();
        // Create HttpHeaders
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", jwt);

        // Create HttpEntity with headers
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        // Send POST request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Get response body
        String responseBody = response.getBody();

        // Parse response JSON and extract userId
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String userId = root.path("userId").asText();
            String nickName = root.path("nickName").asText();
            // Log userId
            System.out.println("UserId: " + userId);
            System.out.println("NickName: " + nickName);
            log.info("id:{}", userId);

            return new AuthDto(userId, nickName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}