package com.example.community_old.Communicator.Alarm;

import com.example.community_old.Communicator.Authentication.AuthDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AlarmDto {
    private final String locationId;
    private final String latitude;
    private final String longitude;
}
