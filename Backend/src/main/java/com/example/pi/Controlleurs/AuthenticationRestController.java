package com.example.pi.Controlleurs;

import com.example.pi.Models.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.pi.Configurations.JwtService;
import com.example.pi.Configurations.SecurityPrincipale;
//import com.example.pi.Models.RegisterWithGoogleRequest;
import com.example.pi.Models.EntityResponse;
import com.example.pi.Models.PasswordResetUtil;
import com.example.pi.Models.RegesterRequest;
import com.example.pi.Models.VerifyVerificationCodeRequest;
import com.example.pi.Services.AuthenticationService;
import com.example.pi.Services.UserServiceImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthenticationRestController {

    public UserServiceImpl userService;
    public AuthenticationService authenticationService;

    private JwtService jwtService;



    @PostMapping("/register")
    public ResponseEntity<AuthenticateResponse> register(
            @RequestBody RegesterRequest request, HttpServletRequest servletRequest){
        log.info("registered");
        try {
            return ResponseEntity.ok(authenticationService.register(request, servletRequest).getBody());
        } catch (MessagingException | UnsupportedEncodingException e) {

            log.error("Error registering user:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<AuthenticateResponse> registeradmin(
            @RequestBody RegesterRequest request, HttpServletRequest servletRequest){
        log.info("registered");
        try {
            return ResponseEntity.ok(authenticationService.registeradmin(request, servletRequest).getBody());
        } catch (MessagingException | UnsupportedEncodingException e) {

            log.error("Error registering user:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticateResponse response = authenticationService.authenticate(request).getBody();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/is-authenticated")
    public  ResponseEntity<Object> isUserAuthenticated(){
        User principale = SecurityPrincipale.getInstance().getLoggedInPrincipal();
        if(principale!=null){
            return EntityResponse.generateResponse("Authorized", HttpStatus.OK , principale);
        }
        return EntityResponse.generateResponse("Unauthorized",HttpStatus.NOT_FOUND, false);
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getUserProfile() {
        String username = SecurityPrincipale.getInstance().getLoggedInPrincipal().username;
        User user = userService.findByUsernameLike(username);
        return EntityResponse.generateResponse("Success", HttpStatus.OK, user);
    }



    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request , HttpServletResponse response)throws IOException{
        authenticationService.refreshToken(request,response);
    }




    @PostMapping("/reset-password")
    public String restPassword(@RequestBody PasswordResetUtil passwordResetUtil,
                               @RequestParam("token") String token){
        User user =
                userService.findByUsernameLike(jwtService.extractUsername(token));


        if(user!=null){
            userService.changePassword(user, passwordResetUtil.getNewPassword());
            return "password reset success";
        }
        return "Invalid password reset token";
    }


    public String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"
                +request.getServerPort()+request.getContextPath();
    }

    @PostMapping("/verify-verification-code")
    public ResponseEntity<String> verifyVerificationCode(@RequestBody VerifyVerificationCodeRequest request) {
        try {
            authenticationService.verifyVerificationCode(request.getUsername(), request.getVerificationCode());
            return ResponseEntity.ok("Verification successful. User account activated.");
        } catch (RuntimeException e) {
            log.error("Error verifying verification code:", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification failed. " + e.getMessage());
        }
    }
    @PostMapping("/forgot-password/{email}")
    public ResponseEntity<String> forgotPassword(@PathVariable String email) {
        try {
            authenticationService.forgotPassword(email);
            return ResponseEntity.ok("New password sent to the user's email.");
        } catch (Exception e) {
            log.error("Error processing forgot password request:", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing forgot password request.");
        }
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = authenticationService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @PutMapping("/role/{userId}")
    public ResponseEntity<User> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> requestBody) {

            ERole newRole = ERole.valueOf(requestBody.get("role"));
            User updatedUser = authenticationService.updateUserRole(userId, newRole);
            return ResponseEntity.ok(updatedUser);

    }
    @PutMapping("/block/{userId}")
    public ResponseEntity<String> blockUser(@PathVariable Long userId) throws MessagingException, UnsupportedEncodingException {
        authenticationService.blockUser(userId);
        return ResponseEntity.ok("User blocked status updated successfully.");
    }

    @PutMapping("/unblock/{userId}")
    public ResponseEntity<String> unblockUser(@PathVariable Long userId) throws MessagingException, UnsupportedEncodingException {
        authenticationService.unblockUser(userId);
        return ResponseEntity.ok("User unblocked status updated successfully.");
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            authenticationService.deleteUserById(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> modifyUserProfile(@PathVariable Long userId, @RequestBody ModifyUserProfileRequest request) {
        return authenticationService.modifyUserProfile(userId, request);
    }

    @PostMapping("/activateExposant/{userId}")
    public ResponseEntity<?> activateExposant(@PathVariable Long userId) {
        try {
            return authenticationService.activateExposant(userId);
        } catch (RuntimeException e) {
            log.error("Error activating exposant account:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error activating exposant account.");
        }
    }
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam Long userId, @RequestParam String code) {
        authenticationService.verifyPhoneNumber(userId, code);
        return ResponseEntity.ok().build();
    }
}
