package com.stationCamera.services;

import com.stationCamera.requests.JwtAuthenticationRequest;
import com.stationCamera.requests.RefreshTokenRequest;
import com.stationCamera.requests.SignInRequest;
import com.stationCamera.requests.SignUpRequest;

public interface AuthenticationService {
    JwtAuthenticationRequest signup(SignUpRequest signUpRequest);
    JwtAuthenticationRequest signIn(SignInRequest signInRequest);
    JwtAuthenticationRequest refreshToken(RefreshTokenRequest refreshTokenRequest);

}
