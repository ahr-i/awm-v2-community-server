package com.example.community_old.monitor;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class Setting {
    private final String monitor;
    private final String serverPort;
    private final String serviceName;

    public Setting() {
        monitor = "http://localhost:5080/service/register";
        serverPort = "9004";
        serviceName = "community_server";
    }
}
