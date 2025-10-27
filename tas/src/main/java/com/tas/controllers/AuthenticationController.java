package com.tas.controllers;

import com.tas.entities.Location;
import com.tas.entities.Station;
import com.tas.repositories.UserRepository;
import com.tas.requests.*;
import com.tas.responses.ApiResponse;
import com.tas.services.AllocationService;
import com.tas.services.AuthenticationService;
import com.tas.services.StationService;
import com.tas.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private AuthenticationService authenticationService;
    private UserRepository userRepository;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    private final StationService stationService;
    private final VehicleService vehicleService;
    private final AllocationService allocationService;
    @Autowired
    public  AuthenticationController(AuthenticationService authenticationService,UserRepository userRepository,StationService stationService,VehicleService vehicleService,AllocationService allocationService){
        this.authenticationService=authenticationService;
        this.stationService=stationService;
        this.userRepository=userRepository;
        this.vehicleService=vehicleService;
        this.allocationService=allocationService;
    }
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody SignUpRequest signUpRequest){
        try {
            return ResponseEntity.ok(new ApiResponse("success",authenticationService.signup(signUpRequest)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse> signIn(@RequestBody SignInRequest signInRequest){
        try {
            return ResponseEntity.ok(new ApiResponse("success",authenticationService.signIn(signInRequest)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/add-person-count/{id}")
    public ResponseEntity<ApiResponse> addPersonCount(@PathVariable Long id, @RequestBody PersonCountRequest personCountRequest){
        try {
            return ResponseEntity.ok(new ApiResponse("success",stationService.addPersonCount(id,personCountRequest)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @GetMapping("/get-station/{id}")
    public ResponseEntity<ApiResponse> getStation(@PathVariable Long id){
        try {
            return ResponseEntity.ok(new ApiResponse("success",stationService.getStationDetails(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @DeleteMapping("/delete-station/{id}")
    public ResponseEntity<ApiResponse> deleteStation(@PathVariable Long id){
        try {
            stationService.deleteStation(id);
            return ResponseEntity.ok(new ApiResponse("success","deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @DeleteMapping("/delete-allocation/{id}")
    public ResponseEntity<ApiResponse> deleteAllocation(@PathVariable Long id){
        try {
            allocationService.deleteAllocation(id);
            return ResponseEntity.ok(new ApiResponse("success","deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id){
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok(new ApiResponse("success","deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @GetMapping("/get-all-stations")
    public ResponseEntity<ApiResponse> getAllStation(){
        try {
            return ResponseEntity.ok(new ApiResponse("success",stationService.getAllStations()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
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

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        try {
            return ResponseEntity.ok(new ApiResponse("success",authenticationService.refreshToken(refreshTokenRequest)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
