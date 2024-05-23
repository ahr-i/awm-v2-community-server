package com.example.CommunityApplication.HttpRequest;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor
@Component
public class HttpRequest {
    public String sendGetRequest(String connectUrl, String userJwtToken) {
        HttpURLConnection connection = null;
        String userName;
        try {
            URL url = new URL(connectUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Accept-Charset", "UTF-8");

            // JWT Token을 헤더에 추가
            connection.setRequestProperty("Authorization", userJwtToken);

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode); // 상태코드

            // JWT Token 받아서 출력
            String jwtToken = connection.getHeaderField("Authorization");
            if (jwtToken != null && jwtToken.startsWith("Bearer")) {
                jwtToken = jwtToken.substring(7);
                System.out.println("JWT Token: " + jwtToken);
            } else {
                System.out.println("No JWT Token");
            }

            // Body 필드
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response Body: " + response.toString());

                userName = response.toString();
                if (userName == null)
                    return null;
                else return userName;
            }
        } catch (Exception e) {
            System.err.println("Error occurred while sending GET request: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    public void sendPostRequest(String connectUrl, String postData) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(connectUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true); // POST 요청을 위해 OutputStream을 사용하도록 설정

            // Header 설정
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Body 데이터 전송
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.write(postData.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode); // 상태코드

            // JWT Token 받아서 출력
            String jwtToken = connection.getHeaderField("Authorization");
            if (jwtToken != null && jwtToken.startsWith("Bearer")) {
                jwtToken = jwtToken.substring(7);
                System.out.println("JWT Token: " + jwtToken);
            } else {
                System.out.println("No JWT Token");
            }

            // Body 필드
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response Body: " + response.toString());
            }

        } catch (Exception e) {
            System.err.println("Error occurred while sending POST request: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
