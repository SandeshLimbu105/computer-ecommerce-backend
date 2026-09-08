package org.texas.computerecommerce.Security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.texas.computerecommerce.Dto.LoginRequestDTO;
import org.texas.computerecommerce.Dto.LoginResponseDto;
import org.texas.computerecommerce.Dto.RegisterRequestDTO;
import org.texas.computerecommerce.Dto.RegisterResponseDto;
import org.texas.computerecommerce.Entity.Enum.RoleType;  // ← IMPORT YOUR ENUM
import org.texas.computerecommerce.Entity.User;
import org.texas.computerecommerce.Repository.UserRepository;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final Jwtutil jwtutil;

    public RegisterResponseDto register(RegisterRequestDTO registerRequestDTO) {

        // 1. Check if email already exists
        if (userRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists with this email!");
        }

        // 2. Determine role (Convert String → Enum)
        RoleType role;
        if (registerRequestDTO.getRole() != null && registerRequestDTO.getRole().equalsIgnoreCase("ADMIN")) {
            role = RoleType.ADMIN;
        } else {
            role = RoleType.CUSTOMER;
        }

        // 3. Create and save user
        User user = User.builder()
                .name(registerRequestDTO.getName())
                .email(registerRequestDTO.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .role(role)  // ← Enum value (RoleType.ADMIN or RoleType.CUSTOMER)
                .build();

        User savedUser = userRepository.save(user);

        // 4. Return response (using the fields that exist in your DTO)
        return RegisterResponseDto.builder()
                .userId(savedUser.getUserId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())  // ← "ADMIN" or "CUSTOMER"
                .message("Registration successful!")
                .build();
    }

    public LoginResponseDto login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
        );
        User user= (User) authentication.getPrincipal();
        String token = jwtutil.generateToken(user.getEmail(), String.valueOf(user.getUserId()));
        return new LoginResponseDto(token,user.getUserId(),user.getName(),user.getEmail(),user.getRole().name(),"login successfully");

    }
}