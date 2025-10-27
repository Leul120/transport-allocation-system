package com.stationCamera.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.stationCamera.entities.User;
import com.stationCamera.repositories.UserRepository;
import com.stationCamera.requests.RefreshTokenRequest;
import com.stationCamera.requests.SignInRequest;
import com.stationCamera.requests.SignUpRequest;
import com.stationCamera.requests.TokenRequest;
import com.stationCamera.response.ApiResponse;
import com.stationCamera.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private AuthenticationService authenticationService;
    private UserRepository userRepository;
    @Autowired
    public  AuthenticationController(AuthenticationService authenticationService,UserRepository userRepository){
        this.authenticationService=authenticationService;
        this.userRepository=userRepository;
    }
//    @PostMapping("/signup")
//    public ResponseEntity<ApiResponse> signup(@RequestBody SignUpRequest signUpRequest){
//        try {
//            return ResponseEntity.ok(new ApiResponse("success",authenticationService.signup(signUpRequest)));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
//        }
//    }
//    @PostMapping("/signin")
//    public ResponseEntity<ApiResponse> signIn(@RequestBody SignInRequest signInRequest){
//        try {
//            return ResponseEntity.ok(new ApiResponse("success",authenticationService.signIn(signInRequest)));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
//        }
//    }
//    @PostMapping("/google")
//    public ResponseEntity<ApiResponse> googleSignUp(@RequestBody TokenRequest request){
//        System.out.println(request);
//        System.out.println(googleClientId);
//        try{
//            GoogleIdTokenVerifier verifier=new GoogleIdTokenVerifier.Builder(
//                    new NetHttpTransport(),
//                    new GsonFactory()
//            ).setAudience(Collections.singletonList(googleClientId))
//                    .build();
//            GoogleIdToken idToken=verifier.verify(request.getCredential());
//            if(idToken!=null){
//                GoogleIdToken.Payload payload=idToken.getPayload();
//                String email=payload.getEmail();
//                String sub=payload.getSubject();
//                String firstName=(String) payload.get("given_name");
//                String lastName=(String) payload.get("family_name");
//                System.out.println(email+sub+firstName+lastName);
//                Optional<User> user=userRepository.findByEmail(email);
//                if(user.isEmpty()){
//                    SignUpRequest signUpRequest=new SignUpRequest();
//                    signUpRequest.setEmail(email);
//                    signUpRequest.setFirstName(firstName);
//                    signUpRequest.setLastName(lastName);
//                    signUpRequest.setPassword(sub);
//                    return ResponseEntity.ok(new ApiResponse("success",authenticationService.signup(signUpRequest)));
//                }else{
//                    SignInRequest signInRequest=new SignInRequest();
//                    signInRequest.setEmail(email);
//                    signInRequest.setPassword(sub);
//                    return ResponseEntity.ok(new ApiResponse("success",authenticationService.signIn(signInRequest)));
//                }
//            }else {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(new ApiResponse("Invalid ID token.",HttpStatus.UNAUTHORIZED));
//            }
//
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse("Error verifying token: " + e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
//        }
//    }
//
//    @PostMapping("/refresh")
//    public ResponseEntity<ApiResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
//        try {
//            return ResponseEntity.ok(new ApiResponse("success",authenticationService.refreshToken(refreshTokenRequest)));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
//        }
//    }
}
