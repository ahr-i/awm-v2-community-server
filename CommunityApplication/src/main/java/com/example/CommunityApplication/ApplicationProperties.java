package com.example.CommunityApplication;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ApplicationProperties {
    @Value("${auth.server.url}")
    private String authServerUrl;
}
