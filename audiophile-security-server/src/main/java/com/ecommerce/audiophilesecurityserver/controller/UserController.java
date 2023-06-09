package com.ecommerce.audiophilesecurityserver.controller;

import com.ecommerce.audiophilesecurityserver.entity.User;
import com.ecommerce.audiophilesecurityserver.exception.InvalidCredentialsException;
import com.ecommerce.audiophilesecurityserver.jwt.JwtTokenProvider;
import com.ecommerce.audiophilesecurityserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static com.ecommerce.audiophilesecurityserver.jwt.JwtTokenFilter.HEADER_PREFIX;

@RestController
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "Authorization")
@RequiredArgsConstructor
@RequestMapping(path = "/users", produces = "application/json")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<User> register(@RequestBody User user) {
        log.info("User details added" + user);
        return new ResponseEntity<>(userService.addUser(user), HttpStatus.CREATED);
    }

    @PutMapping(value = "/update", consumes = "application/json")
    public ResponseEntity<User> updateUser(@RequestBody User user, @RequestHeader(name = "Authorization") String token) {
        String mailId = userService.getMailIdFromToken(token);
        return new ResponseEntity<>(userService.updateUser(user, mailId), HttpStatus.CREATED);
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<HttpStatus> deleteAllUsers() {
        userService.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("mailId") String mailId) {
        String token = null;
        if (userService.loadByMailId(mailId).isEnabled()) {
            token = userService.saveForgotPasswordToken(mailId);
        }

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @GetMapping("/verify-token")
    public ResponseEntity<Boolean> resetPassword(@RequestParam("token") String token, @RequestParam("mailId") String mailId) {
        return new ResponseEntity<>(userService.verifyPasswordToken(mailId, token), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<User> resetPassword(@RequestParam("token") String token, @RequestBody AuthRequest authRequest) {
        return new ResponseEntity<>(userService.updatePassword(authRequest, token), HttpStatus.OK);
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<String> signIn(@RequestBody AuthRequest authRequest) {
        log.info("Login started");
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getMailId(), authRequest.getPassword()));
            log.info("Authenticated");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException(e.getMessage());
        }

        String token = jwtTokenProvider.createToken(authentication);

        log.info(token);
        if (!userService.checkUserVerification(authRequest.getMailId())) {
            return new ResponseEntity<>("User not verified", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @GetMapping("/mail-id")
    public ResponseEntity<String> getMailIdFromToken(@RequestHeader(name = "Authorization") String token) {
        return new ResponseEntity<>(userService.getMailIdFromToken(token), HttpStatus.OK);
    }

    @GetMapping("/fullname")
    public ResponseEntity<String> getFullname(@RequestHeader(name = "Authorization") String token) {
        String mailId = userService.getMailIdFromToken(token);
        return new ResponseEntity<>(userService.getFullname(mailId), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUser(@RequestHeader(name = "Authorization") String token) {
        String mailId = userService.getMailIdFromToken(token);
        return new ResponseEntity<>(userService.loadByMailId(mailId), HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader(name = "Authorization") String token) {
        String jwtToken = token.substring(HEADER_PREFIX.length()).trim();

        if (jwtTokenProvider.validateToken(jwtToken)) {
            log.info("Validated");
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            log.info("Not validated");
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
    }

    @GetMapping("/status-check")
    public ResponseEntity<String> statusCheck(@RequestHeader(name = "Authorization") String token) {
        return new ResponseEntity<>("Ok", HttpStatus.OK);
    }


}
