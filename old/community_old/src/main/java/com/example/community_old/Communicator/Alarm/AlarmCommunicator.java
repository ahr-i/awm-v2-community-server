package com.example.community_old.Communicator.Alarm;

import com.example.community_old.Communicator.CommSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmCommunicator {
    private final CommSetting setting;

    public void alarm(int locationId, double latitude, double longitude) {
        String url = setting.getAlarmAddress() + "/verify/";

        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Create HttpHeaders
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("location_id", String.valueOf(locationId));
        requestBody.put("latitude", String.valueOf(latitude));
        requestBody.put("longitude", String.valueOf(longitude));

        // Convert request body to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody;
        try {
            jsonRequestBody = objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            log.warn("Failed to convert request body to JSON.", e);
            return;
        }

        // Create HttpEntity with headers and body
        HttpEntity<String> entity = new HttpEntity<>(jsonRequestBody, headers);

        try {
            // Send POST request
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            log.info("Response from alarm server: {}", response.getBody());
        } catch (Exception e) {
            log.error("Error sending alarm request to server", e);
        }
    }
}
