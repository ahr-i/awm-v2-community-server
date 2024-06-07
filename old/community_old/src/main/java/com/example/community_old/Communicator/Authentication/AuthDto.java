package com.example.community_old.Communicator.Authentication;

import jakarta.persistence.GeneratedValue;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AuthDto {
    private final String userId;
    private final String nickName;
}
