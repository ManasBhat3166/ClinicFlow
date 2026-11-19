package com.clinic.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.clinic.dto.LoginRequest;
import com.clinic.dto.LoginResponse;
import com.clinic.entity.User;
import com.clinic.repository.UserRepository;
import com.clinic.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByClinicIdAndMobile(
                        request.getClinicId(),
                        request.getMobile()
                )
                .orElseThrow(() ->
                        new RuntimeException("Invalid mobile number or password"));

        if (!user.isActive()) {
            throw new RuntimeException("User account is inactive");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            throw new RuntimeException("Invalid mobile number or password");
        }

        String token = jwtService.generateToken(
                user.getMobile(),
                user.getId(),
                user.getClinicId(),
                user.getRole()
        );

        return new LoginResponse(
                token,
                user.getId(),
                user.getClinicId(),
                user.getName(),
                user.getMobile(),
                user.getRole()
        );
    }
}