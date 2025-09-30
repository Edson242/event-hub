package com.edson.eventHub.dto;

import com.edson.eventHub.entities.User;
import com.edson.eventHub.enuns.Role;
import lombok.Data;

@Data
public class UserResponseDTO {
    
    private Integer id;
    private String name;
    private String email;
    private Role role;

    // Método de conversão: Transforma uma entidade User em um UserResponseDTO
    public static UserResponseDTO fromEntity(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}