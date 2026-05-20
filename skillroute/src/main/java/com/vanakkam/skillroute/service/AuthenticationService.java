package com.vanakkam.skillroute.service;

import com.vanakkam.skillroute.dto.AuthenticationRequest;
import com.vanakkam.skillroute.dto.AuthenticationResponse;
import com.vanakkam.skillroute.dto.RegisterRequest;
import com.vanakkam.skillroute.model.Role;
import com.vanakkam.skillroute.model.User;
import com.vanakkam.skillroute.repository.UserRepository;
import com.vanakkam.skillroute.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        // 1. Build the user object and hash the password
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .build();

        // 2. Save to PostgreSQL
        repository.save(user);

        // 3. Generate JWT token
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. This checks if the email and password match in the DB. If not, it throws an error.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. If we get here, the user is authenticated. Fetch them to generate a token.
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}