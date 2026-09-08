package org.texas.computerecommerce.Controller;

import org.texas.computerecommerce.Dto.LoginRequestDTO;
import org.texas.computerecommerce.Dto.LoginResponseDto;
import org.texas.computerecommerce.Dto.RegisterRequestDTO;
import org.texas.computerecommerce.Dto.RegisterResponseDto;
import org.texas.computerecommerce.Security.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        RegisterResponseDto response = authService.register(registerRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDto response = authService.login(loginRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}