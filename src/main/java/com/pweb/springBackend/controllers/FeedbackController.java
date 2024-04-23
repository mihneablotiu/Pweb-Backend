package com.pweb.springBackend.controllers;

import com.pweb.springBackend.DTOs.requests.FeedbackDTO;
import com.pweb.springBackend.DTOs.responses.ErrorResponseDTO;
import com.pweb.springBackend.DTOs.responses.ResponseDTO;
import com.pweb.springBackend.DTOs.responses.ValidResponseDTO;
import com.pweb.springBackend.configs.JwtUtil;
import com.pweb.springBackend.entities.Feedback;
import com.pweb.springBackend.entities.User;
import com.pweb.springBackend.enums.UserRole;
import com.pweb.springBackend.repositories.FeedbackRepository;
import com.pweb.springBackend.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final JwtUtil jwtUtil;

    public FeedbackController(UserRepository userRepository, FeedbackRepository feedbackRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.feedbackRepository = feedbackRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> createFeedback(@RequestBody FeedbackDTO feedbackDTO, @RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        if (user == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("User not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        if (feedbackDTO.getAcceptTerms().equals(false)) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("You must accept the terms to create a feedback", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        Feedback feedback = new Feedback();
        feedback.setFeedbackType(feedbackDTO.getFeedbackType());
        feedback.setAcceptTerms(feedbackDTO.getAcceptTerms());
        feedback.setSatisfactionLevel(feedbackDTO.getSatisfactionLevel());
        feedback.setComment(feedbackDTO.getComment());
        feedback.setUser(user);

        feedbackRepository.save(feedback);

        ValidResponseDTO validResponseDTO = new ValidResponseDTO("Feedback created successfully!", HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(validResponseDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteFeedback(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        if (user == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("User not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        if (jwtUtil.getRole(claims).equals(UserRole.NORMAL.toString())) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Unauthorized for this operation", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponseDTO);
        }

        Feedback feedback = feedbackRepository.findById(id).orElse(null);
        if (feedback == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Feedback not found", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
        }

        feedbackRepository.delete(feedback);

        ValidResponseDTO validResponseDTO = new ValidResponseDTO("Feedback deleted successfully!", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(validResponseDTO);
    }
}
