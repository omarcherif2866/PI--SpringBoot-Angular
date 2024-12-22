package com.example.pi.Models;

import com.example.pi.Models.ERole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegesterRequest {

    public String username;
    public String email;
    public String password;
    public String tel;
    public String image;
    public ERole role;
}
