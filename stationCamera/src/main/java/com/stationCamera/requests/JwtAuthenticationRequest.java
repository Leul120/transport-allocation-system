package com.stationCamera.requests;

import lombok.Data;

@Data
public class JwtAuthenticationRequest {
    private String token;
    private String refreshToken;
}
