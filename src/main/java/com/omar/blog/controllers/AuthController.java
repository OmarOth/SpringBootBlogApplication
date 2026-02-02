package com.omar.blog.controllers;

import com.omar.blog.domain.dto.AuthResponse;
import com.omar.blog.domain.dto.LoginRequest;
import com.omar.blog.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/auth/login")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationService authenticationService;

  @PostMapping
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
    UserDetails userDetails =
        authenticationService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

    String token = authenticationService.generateToken(userDetails);

    AuthResponse authResponse =
        AuthResponse.builder().token(token).expiresIn(60 * 60 * 24).build(); // 24 hours

    return ResponseEntity.ok(authResponse);
  }
}
