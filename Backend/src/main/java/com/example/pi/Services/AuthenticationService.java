package com.example.pi.Services;
        import com.example.pi.Controlleurs.UserNotFoundException;
        import com.fasterxml.jackson.core.JsonProcessingException;
        import com.fasterxml.jackson.databind.JsonNode;
        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.warrenstrange.googleauth.GoogleAuthenticator;
        import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
        import com.warrenstrange.googleauth.GoogleAuthenticatorException;
        import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
        import jakarta.annotation.PostConstruct;
        import jakarta.mail.MessagingException;
        import jakarta.mail.internet.MimeMessage;
        import jakarta.persistence.EntityNotFoundException;
        import jakarta.servlet.http.HttpServletRequest;
        import jakarta.servlet.http.HttpServletResponse;
        import lombok.AllArgsConstructor;
        import lombok.extern.slf4j.Slf4j;
        import net.glxn.qrgen.core.image.ImageType;
        import net.glxn.qrgen.javase.QRCode;
        import org.apache.coyote.BadRequestException;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.context.ApplicationEventPublisher;
        import org.springframework.core.io.ByteArrayResource;
        import org.springframework.http.*;
        import org.springframework.mail.javamail.JavaMailSender;
        import org.springframework.mail.javamail.MimeMessageHelper;
        import org.springframework.security.access.prepost.PreAuthorize;
        import org.springframework.security.authentication.AuthenticationManager;
        import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;
        import com.example.pi.Configurations.JwtService;
        import com.example.pi.Models.*;
        import com.example.pi.Repositories.UserRepository;
        import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
        import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
        import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
        import com.google.api.client.json.JsonFactory;
        import com.google.api.client.json.jackson2.JacksonFactory;
        import lombok.extern.slf4j.Slf4j;
        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.UnsupportedEncodingException;
        import java.security.GeneralSecurityException;
        import java.security.SecureRandom;
        import java.util.*;

        import org.springframework.util.LinkedMultiValueMap;
        import org.springframework.util.MultiValueMap;
        import org.springframework.web.client.RestTemplate;
        import org.springframework.web.multipart.MultipartFile;
        import com.fasterxml.jackson.databind.JsonNode;
        import com.fasterxml.jackson.databind.ObjectMapper;
        import static com.twilio.constant.EnumConstants.ContentType.JSON;



        import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender javaMailSender;
    private final ApplicationEventPublisher eventPublisher;

    private final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private GoogleIdTokenVerifier verifier;
    private final PhoneVerificationService twilioService;



    public ResponseEntity<?> verifyPhoneNumber(Long userId, String userInputCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify the code entered by the user
        if (userInputCode.equals(user.getTelVerifcode())) {
            // Update telVerif field to true if verification succeeds
            user.setTelVerif(true);
            userRepository.save(user);
            return ResponseEntity.ok("Phone number verified successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid verification code.");
        }
    }




    @Transactional
    public ResponseEntity<AuthenticateResponse> register(RegesterRequest request, HttpServletRequest servletRequest) throws MessagingException, UnsupportedEncodingException {
        // Generate a secret key for 2FA
        String verificationCode = generateVerificationCode(6);
        String secretKey = generate2FASecretKey();

        // Create the user with 2FA enabled and set the secret key
        var user = User.builder()
                .username(request.getUsername())
                .activated(true)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .tel(request.getTel())
                .image(request.getImage())
                .role(ERole.ROLE_MEMBRE)
                .role(request.role.ROLE_MEMBRE)
                .verificationCode(generateVerificationCode(6))
                .telVerifcode(verificationCode)
                .activation2fa(true)
                .telVerif(false)
                .twofactorcode(secretKey) // Set the 2FA secret key for the user

                .build();
        userRepository.save(user);

        // Send verification code via Twilio
        //twilioService.sendVerificationCode(user.getTel(), verificationCode);

        // Schedule deactivation task
        scheduleDeactivation(user);
        userRepository.save(user);
     /*   if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }


        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }*/
        // Send email with 2FA code and QR code
        send2FACodeEmail(user);

        // Schedule deactivation task


        // Generate JWT tokens
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Create response
        AuthenticateResponse response = AuthenticateResponse.builder()
                .acessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.ok(response);
    }


    private void scheduleDeactivation(User user) {
        TimerTask task = new TimerTask() {
            public void run() {
                if (!user.isTelVerif()) {
                    // Deactivate account if phone number is not verified
                    user.setActivated(false);
                    userRepository.save(user);
                    // Log deactivation
                }
            }
        };

        Timer timer = new Timer(true);
        timer.schedule(task, 60 * 60 * 1000); // 1 hour delay for deactivation
    }


    private void send2FACodeEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String subject = "2FA Activation";
        String senderName = "ESPRIT";
        String mailContent = "<p> Hi, " + user.getUsername() + ", </p>" +
                "<p>Thank you for registering with us!</p>" +
                "<p>Please use the following code to activate 2FA in your account:</p>" +
                "<p><b>" + user.getTwofactorcode() + "</b></p>" +
                "<p>Alternatively, you can scan the QR code below with the Google Authenticator app.</p>";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom("your-email@example.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);

        // Add the HTML content
        messageHelper.setText(mailContent, true);

        // Attach the QR code image
        byte[] qrCodeImageBytes = generateQRCode(user.getUsername(), user.getTwofactorcode());
        messageHelper.addInline("qrCode", new ByteArrayResource(qrCodeImageBytes), "image/png");

        javaMailSender.send(message);
    }

    private byte[] generateQRCode(String email, String secretKey) {
        // Construct the URL for the QR code
        String qrCodeUrl = "otpauth://totp/" + email + "?secret=" + secretKey;


        ByteArrayOutputStream outputStream = QRCode.from(qrCodeUrl)
                .to(ImageType.PNG)
                .stream();

        return outputStream.toByteArray();
    }
    private void scheduleDeactivationTask(User user) {
        TimerTask task = new TimerTask() {
            public void run() {

                if (user.getVerificationCodeCheck() == user.verificationCode) {
                    user.setActivated(false);
                    userRepository.save(user);
                    log.info("Account deactivated due to non-verification: {}", user.getUsername());
                }
            }
        };

        Timer timer = new Timer(true);
        timer.schedule(task, 100 * 30 * 1000);
    }

    private String generateVerificationCode(int length) {
        String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom RANDOM = new SecureRandom();
        StringBuilder verificationCode = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            verificationCode.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return verificationCode.toString();
    }

    private void sendVerificationEmail(User user, HttpServletRequest servletRequest) throws MessagingException, UnsupportedEncodingException {
        String verificationUrl = user.getVerificationCode();
        String subject = "Email Verification";
        String senderName = "ESPRIT";
        String mailContent = "<p> Hi, " + user.getUsername() + ", </p>" +
                "<p><b>Thank you for registering with us!</b>" + "" +
                "Please, follow the link below to verify your email address.</p>" +
                verificationUrl  +
                "<p> Your Application Name";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("piemepieme494@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
        userRepository.save(user);

    }





    public ResponseEntity<AuthenticateResponse> authenticate(AuthenticationRequest request) {
        try {
            log.info("Authenticating user: {}", request.getUsername());

            // Fetch the user from the database
            var user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("User found: {}", user.getUsername());

            // Verify the password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.error("Invalid password for user: {}", request.getUsername());
                throw new RuntimeException("Bad credentials");
            }

            // Perform authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            if (!user.isActivated()) {
                throw new RuntimeException("User account is not activated.");
            }

            // Check if 2FA is activated for the user
            if (user.isActivation2fa()) {
                log.info("2FA is enabled for user: {}", user.getUsername());
                boolean isVerified = verify2FA(user.getTwofactorcode(), request.getTwofactorcode());

            }

            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);

            AuthenticateResponse response = AuthenticateResponse.builder()
                    .acessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .tel(user.getTel())
                    .image(user.getImage())
                    .verificationCode(user.getVerificationCode())
                    .role(user.getRole())
                    .verificationCodeCheck(user.getVerificationCodeCheck())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    // Function to verify the 2FA code
    private boolean verify2FA(String secretKey, String code) {
        try {
            int codeInt = Integer.parseInt(code);
            GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
            // Assuming the GoogleAuthenticator's authorize method can work with a String code
            return googleAuthenticator.authorize(secretKey, codeInt);
        } catch (NumberFormatException e) {
            // Handle the case where the code is not a valid integer
            log.error("Invalid 2FA code format: {}", code);
            return false;
        }
    }
   /*     try {
            int codeInt = Integer.parseInt(code);
            GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
            // Assuming the GoogleAuthenticator's authorize method can work with a String code
            return googleAuthenticator.authorize(secretKey, codeInt);
        } catch (NumberFormatException e) {
            // Handle the case where the code is not a valid integer
            System.err.println("Invalid 2FA code format: " + code);
            return false;
        }
    }*/

    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var user = this.userRepository.findByUsername(username)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken,  user)) {
                var accessToken = jwtService.generateToken( user);
                var authResponse = AuthenticateResponse.builder()
                        .acessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
    @Transactional
    public void verifyVerificationCode(String username, String verificationCode) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (verificationCode.equals(user.getVerificationCode())) {
            user.setVerificationCodeCheck(verificationCode);
            user.setActivated(true);
            userRepository.save(user);
            log.info("Account activated: {}", username);
        } else {

            log.error("Incorrect verification code for user: {}", username);

            throw new RuntimeException("Incorrect verification code");
        }
    }
    @Transactional
    public void forgotPassword(String email) throws MessagingException, UnsupportedEncodingException {
        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        String newPassword = generateRandomPassword(10);


        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);


        sendNewPasswordEmail(user, newPassword);
    }
    private String generateRandomPassword(int length) {
        String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom RANDOM = new SecureRandom();
        StringBuilder newPassword = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            newPassword.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return newPassword.toString();
    }
    private void sendNewPasswordEmail(User user, String newPassword) throws MessagingException, UnsupportedEncodingException {
        String subject = "New Password";
        String senderName = "ESPRIT";
        String mailContent = "<p> Hi, " + user.getUsername() + ", </p>" +
                "<p>Your new password is: <b>" + newPassword + "</b></p>" +
                "<p>Please keep this password secure and consider changing it after logging in.</p>" +
                "<p> Your Application Name";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("your-email@example.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Boolean unblockUser(Long id) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findById(id).orElse(null);

        if (user != null && !user.isActivated()) {
            user.setActivated(true);
            userRepository.save(user);
            // Optionally, you can send an email notifying the user of their activation
            sendunBanEmail(user);
            return true; // User successfully unblocked
        }

        return false; // User not found or already activated
    }

    /*public String oauthLinkedin(String code) throws Exception {
        String clientId = "77lz7wunm0s5jn";
        String clientSecret = "24uSXbMPN9VSXjPm";
        String redirectUri = "http://127.0.0.1:4200/callback";

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<AccessTokenResponse> responseEntity = restTemplate.exchange(
                "https://www.linkedin.com/oauth/v2/accessToken",
                HttpMethod.POST,
                requestEntity,
                AccessTokenResponse.class);

        AccessTokenResponse accessTokenResponse = responseEntity.getBody();

        if (accessTokenResponse != null) {
            String accessToken = accessTokenResponse.getAccess_token();

            LinkedInUserInfo userProfile = fetchLinkedinProfile(accessToken);
            Optional<User> existingUser = userRepository.findByEmail(userProfile.getEmail());
            if (existingUser.isPresent()) {
                UserDetailsImpl userDetailsImpl = new UserDetailsImpl(existingUser.get());
                existingUser.get().setPassword(new BCryptPasswordEncoder().encode("default"));
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(userDetailsImpl.getUsername(), "default")
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return jwtUtil.generateJwtToken(authentication);
            } else {
                User savedUser = new User(userProfile.getName(), userProfile.getFamilyName(), userProfile.getName().charAt(0) + userProfile.getFamilyName(), userProfile.getEmail(), "");
                savedUser.setProfilePicture(userProfile.getPicture());
                savedUser.setActive(true);
                savedUser.setRole(ERole.ROLE_USER);
                userRepository.save(savedUser);
                savedUser.setPassword(new BCryptPasswordEncoder().encode("default"));
                UserDetailsImpl userDetailsImpl = new UserDetailsImpl(savedUser);
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(userDetailsImpl.getUsername(), "default")
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return jwtUtil.generateJwtToken(authentication);
            }
        } else {
            throw new BadRequestException("Failed to obtain access token");
        }
    }

    private LinkedInUserInfo fetchLinkedinProfile(String accessToken) throws BadRequestException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<LinkedInUserInfo> responseEntity = restTemplate.exchange(
                "https://api.linkedin.com/v2/userinfo",
                HttpMethod.GET,
                entity,
                LinkedInUserInfo.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            throw new BadRequestException("Failed to fetch LinkedIn profile");
        }
    }
*/

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Boolean blockUser(Long id) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findById(id).orElse(null);

        if (user != null && user.isActivated()) {
            user.setActivated(false); // Assuming 'activated' controls access
            userRepository.save(user);
            sendBanEmail(user);

            return true; // User successfully blocked
        }

        return false; // User not found or already blocked
    }
    private void sendunBanEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String subject = "Welcome Back";
        String senderName = "ESPRIT";
        String mailContent = "<p> Hi, " + user.getUsername() + ", </p>" +
                "<p>We hope this message finds you well. We are pleased to inform you that your account has been successfully reactivated. Your access has been restored, effective immediately.</p>" +
                "<p><b>We understand that the suspension of your account for the past month was due to a violation of our privacy and policy terms. Please accept our sincerest apologies for any inconvenience this may have caused you.</b></p>";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom("your-email@example.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);

        // Add the HTML content
        messageHelper.setText(mailContent, true);


        javaMailSender.send(message);
    }
    private void sendBanEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String subject = "Ban User";
        String senderName = "ESPRIT";
        String mailContent = "<p> Hi, " + user.getUsername() + ", </p>" +
                "<p>We regret to inform you that your account has been temporarily suspended for violating our privacy and policy terms. </p>" +
                "<p><b>This suspension is effective immediately and will last for one month, starting from the mail receive</b></p>";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom("your-email@example.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);

        // Add the HTML content
        messageHelper.setText(mailContent, true);


        javaMailSender.send(message);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findByRole(ERole.ROLE_MEMBRE, ERole.ROLE_RESPONSABLE);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    public User updateUserRole(Long userId, ERole newRole) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRole(newRole);
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Transactional
    public ResponseEntity<?> modifyUser(Long userId, RegesterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        user.setTel(request.getTel());
        user.setImage(request.getImage());


        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
    public ResponseEntity<?> activate2FA(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        String secretKey = generate2FASecretKey();

        user.setActivation2fa(true);
        user.setTwofactorcode(secretKey);

        userRepository.save(user);


        return ResponseEntity.ok("2FA activated. Secret key: " + secretKey);
    }


    public String generate2FASecretKey() {
        // Create a Google Authenticator instance
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(new GoogleAuthenticatorConfig());

        // Generate a secret key
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();

        // Return the secret key as a string
        return key.getKey();
    }


    @Transactional
    public ResponseEntity<?> modifyUserProfile(Long userId, ModifyUserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check if the old password matches before allowing modification
        if (request.getOldPassword() != null && !request.getOldPassword().isEmpty()) {
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new RuntimeException("Incorrect old password");
            }
        }

        // Update the user's account information
        user.setUsername(request.getUsername());
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setEmail(request.getEmail());
        user.setTel(request.getTel());
        user.setImage(request.getImage());

        // If a new password is provided, encode and update the password
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<AuthenticateResponse> registeradmin(RegesterRequest request, HttpServletRequest servletRequest) throws MessagingException, UnsupportedEncodingException {
        // Generate a secret key for 2FA
        String verificationCode = generateVerificationCode(6);
        String secretKey = generate2FASecretKey();

        // Create the user with 2FA enabled and set the secret key
        var user = User.builder()
                .username(request.getUsername())
                .activated(true)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .tel(request.getTel())
                .image(request.getImage())
                .role(ERole.ROLE_ADMIN)
                .role(request.role.ROLE_RESPONSABLE)
                .role(request.role.ROLE_RESPONSABLE)
                .verificationCode(generateVerificationCode(6))
                .telVerifcode(verificationCode)
                .activation2fa(true)
                .telVerif(false)
                .twofactorcode(secretKey) // Set the 2FA secret key for the user

                .build();
        userRepository.save(user);

        // Send verification code via Twilio
        //twilioService.sendVerificationCode(user.getTel(), verificationCode);

        // Schedule deactivation task
        scheduleDeactivation(user);
        userRepository.save(user);

        // Send email with 2FA code and QR code
        send2FACodeEmail(user);

        // Schedule deactivation task


        // Generate JWT tokens
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Create response
        AuthenticateResponse response = AuthenticateResponse.builder()
                .acessToken(jwtToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .tel(user.getTel())
                .image(user.getImage())
                .verificationCode(user.getVerificationCode())
                .role(user.getRole())
                .build();
        return ResponseEntity.ok(response);
    }

    private void send2FACodeEmailForExposant(User user) throws MessagingException, UnsupportedEncodingException {
        String subject = "2FA Activation";
        String senderName = "BUGBUTTLERS";
        String mailContent = "<p> Hi, " + user.getUsername() + ", </p>" +
                "<p>Thank you for registering with us!</p>" +
                "<p>Please use the following code to activate 2FA in your account:</p>" +
                "<p><b>" + user.getTwofactorcode() + "</b></p>" +
                "<p>Alternatively, you can scan the QR code below with the Google Authenticator app.</p>" +
                "<p>Please note that your account is currently pending approval from the admin. You will be notified via email once your account is approved.</p>";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom("your-email@example.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);

        // Add the HTML content
        messageHelper.setText(mailContent, true);

        // Attach the QR code image
        byte[] qrCodeImageBytes = generateQRCode(user.getUsername(), user.getTwofactorcode());
        messageHelper.addInline("qrCode", new ByteArrayResource(qrCodeImageBytes), "image/png");

        javaMailSender.send(message);
    }
    @Transactional
    public ResponseEntity<?> activateExposant(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActivated(true);

        // Save the updated user
        userRepository.save(user);

        // Send email to the user
        try {
            sendExposantActivationEmail(user);
        } catch (MessagingException | UnsupportedEncodingException e) {
            // Handle any exceptions related to sending email
            e.printStackTrace(); // Log the error or handle it as needed
        }

        return ResponseEntity.ok("Exposant account activated: " + user.getUsername());
    }

    private void sendExposantActivationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String subject = "Exposant Account Activation";
        String senderName = "BUGBUTTERS";
        String mailContent = "<p>Hi, " + user.getUsername() + ",</p>" +
                "<p>Your exposant account has been successfully activated.</p>" +
                "<p>You can now access your account and start using our services.</p>";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("your-email@example.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
    }

}