package com.example.pi.Models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyUserProfileRequest {
    private String username;
    private String nom;
    private String prenom;
    private String email;
    private String tel;
    private String image;
    private String oldPassword;
    private String newPassword;
}
