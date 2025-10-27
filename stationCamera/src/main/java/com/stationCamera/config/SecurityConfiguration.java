package com.stationCamera.config;

import com.stationCamera.entities.Role;
import com.stationCamera.services.UserService;
import com.stationCamera.utils.CustomCorsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    private final CustomCorsConfiguration customCorsConfiguration;
    @Autowired
    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,UserService userService,CustomCorsConfiguration customCorsConfiguration){
        this.jwtAuthenticationFilter=jwtAuthenticationFilter;
        this.userService=userService;
        this.customCorsConfiguration=customCorsConfiguration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        try {
            http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(request->request.requestMatchers("/api/v1/auth/**").permitAll().requestMatchers("/api/v1/admin").hasAnyAuthority(Role.ADMIN.name()).requestMatchers("/api/v1/user").hasAnyAuthority(Role.USER.name()).anyRequest().authenticated())
                    .sessionManagement(manager->manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                    .cors(c->c.configurationSource(customCorsConfiguration))
                    .authenticationProvider(authenticationProvider()).addFilterBefore(
                            jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
                    );
            return http
                    .build();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        }
    @Bean
    public AuthenticationProvider authenticationProvider(){
            DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
            authenticationProvider.setUserDetailsService(userService.userDetailsService());
            authenticationProvider.setPasswordEncoder(passwordEncoder());
            return authenticationProvider;
        }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
    }

