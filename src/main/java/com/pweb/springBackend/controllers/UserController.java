package com.pweb.springBackend.controllers;

import com.pweb.springBackend.DTOs.requests.UserLoginDTO;
import com.pweb.springBackend.DTOs.requests.UserRegisterDTO;
import com.pweb.springBackend.DTOs.requests.UserUpdateRoleDTO;
import com.pweb.springBackend.DTOs.responses.ErrorResponseDTO;
import com.pweb.springBackend.DTOs.responses.ResponseDTO;
import com.pweb.springBackend.DTOs.responses.UserLoginResponseDTO;
import com.pweb.springBackend.DTOs.responses.ValidResponseDTO;
import com.pweb.springBackend.configs.JwtUtil;
import com.pweb.springBackend.entities.User;
import com.pweb.springBackend.enums.UserRole;
import com.pweb.springBackend.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserController(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody UserRegisterDTO userRegisterDto) {
        if (!EmailValidator.getInstance().isValid(userRegisterDto.getEmail())) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Invalid email!", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Email already exists!", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        User user = new User();
        user.setUsername(userRegisterDto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userRegisterDto.getPassword()));
        user.setEmail(userRegisterDto.getEmail());
        user.setRole(UserRole.NORMAL);

        userRepository.save(user);

        ValidResponseDTO validResponse = new ValidResponseDTO("User registered successfully!", HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(validResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody UserLoginDTO loginReq)  {
        User user = userRepository.findByEmail(loginReq.getEmail()).orElse(null);
        if (user == null) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("User not registered", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReq.getEmail(), loginReq.getPassword()));
            String token = jwtUtil.createToken(user);

            UserLoginResponseDTO loginRes = new UserLoginResponseDTO(token, loginReq.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(loginRes);

        } catch (BadCredentialsException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Invalid username or password", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PatchMapping("/update-role")
    public ResponseEntity<ResponseDTO> updateRole(@RequestBody UserUpdateRoleDTO userUpdateRoleDTO, @RequestHeader("Authorization") String token) {
        User user = userRepository.findByEmail(userUpdateRoleDTO.getEmail()).orElse(null);
        if (user == null) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("User not registered", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        if (jwtUtil.getRole(claims).equals(UserRole.NORMAL.toString())) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Unauthorized for this operation", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            user.setRole(UserRole.valueOf(userUpdateRoleDTO.getRole()));
            userRepository.save(user);

            ValidResponseDTO validResponse = new ValidResponseDTO("User role updated successfully!", HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.OK).body(validResponse);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Invalid role", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable String email, @RequestHeader("Authorization") String token) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("User not registered", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        if (jwtUtil.getRole(claims).equals(UserRole.NORMAL.toString())) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Unauthorized for this operation", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        userRepository.delete(user);

        ValidResponseDTO validResponse = new ValidResponseDTO("User deleted successfully!", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(validResponse);
    }
}
