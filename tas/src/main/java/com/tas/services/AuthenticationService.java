package com.tas.services;


import com.tas.requests.JwtAuthenticationRequest;
import com.tas.requests.RefreshTokenRequest;
import com.tas.requests.SignInRequest;
import com.tas.requests.SignUpRequest;

public interface AuthenticationService {
    JwtAuthenticationRequest signup(SignUpRequest signUpRequest);
    JwtAuthenticationRequest signIn(SignInRequest signInRequest);
    JwtAuthenticationRequest refreshToken(RefreshTokenRequest refreshTokenRequest);

}
