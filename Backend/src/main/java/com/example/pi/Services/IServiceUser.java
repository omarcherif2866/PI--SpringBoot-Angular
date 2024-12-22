package com.example.pi.Services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import com.example.pi.Models.ERole;
import com.example.pi.Models.User;

import java.io.IOException;
import java.util.List;

public interface IServiceUser {

    String saveImageForUsers(MultipartFile file) throws IOException;


    Boolean blockUser(Long id);

    List<User> getAllUserWithRole(ERole role1, ERole role2);

    boolean createPasswordResetToken(String token , UserDetails userDetails);

    String generateToken(User user);

    void changePassword(User user , String newPassword);
}
