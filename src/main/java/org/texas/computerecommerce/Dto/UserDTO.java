package org.texas.computerecommerce.Dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String name;
    private String email;
    private String role;
    // NO passwordHash here!
}