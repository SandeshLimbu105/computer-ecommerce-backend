package org.texas.computerecommerce.Dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String name;
    private String email;
    private String password;
    private String role;  // Optional: "CUSTOMER" or "ADMIN"
}