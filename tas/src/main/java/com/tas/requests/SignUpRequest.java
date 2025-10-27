package com.tas.requests;

import com.tas.entities.Vehicle;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Vehicle vehicle;
}
