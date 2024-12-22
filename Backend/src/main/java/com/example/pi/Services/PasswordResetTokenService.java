package com.example.pi.Services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.pi.Models.AuthenticateResponse;
import com.example.pi.Models.User;

@Service
@AllArgsConstructor
public class PasswordResetTokenService {

    public void createPasswordResetTokenUser(User user , String token){

        AuthenticateResponse response = new AuthenticateResponse();
    }
}
