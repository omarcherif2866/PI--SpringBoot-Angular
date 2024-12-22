package com.example.pi.Models;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class rolerequest {

    @lombok.Getter
    @Enumerated(EnumType.STRING)
    public ERole role ;

    public void setRole(String role) {
        this.role = ERole.valueOf(role);
    }
}
