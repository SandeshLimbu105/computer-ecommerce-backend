package org.texas.computerecommerce.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDto {
    private Long userId;
    private String name;
    private String email;      // ← ADD THIS
    private String role;       // ← ADD THIS
    private String message;
}