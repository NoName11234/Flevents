package de.flyndre.fleventsbackend.security.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LogoutRequest {
    private String token;
    private String deviceInfo;
}
