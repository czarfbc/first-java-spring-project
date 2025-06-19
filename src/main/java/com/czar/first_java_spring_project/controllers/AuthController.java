package com.czar.first_java_spring_project.controllers;

import com.czar.first_java_spring_project.domain.User;
import com.czar.first_java_spring_project.dto.request.SignInRequestDto;
import com.czar.first_java_spring_project.dto.request.SignUpRequestDto;
import com.czar.first_java_spring_project.dto.response.SignInResponseDto;
import com.czar.first_java_spring_project.dto.response.SignUpResponseDto;
import com.czar.first_java_spring_project.infra.security.TokenService;
import com.czar.first_java_spring_project.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponseDto> signUp(@RequestBody SignUpRequestDto body) {
        Optional<User> user = this.userRepository.findUserByEmail(body.email());

        if(user.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User newUser = new User();
        newUser.setEmail(body.email());
        newUser.setPassword(this.passwordEncoder.encode(body.password()));
        newUser.setName(body.name());
        newUser.setTermsAndCondictionsAccepted(body.termsAndCondictionsAccepted());
        newUser.setNewsLetterSubscriber(body.newsLetterSubscriber());

        this.userRepository.save(newUser);

        String token = this.tokenService.generateToken(newUser);
        return ResponseEntity.ok(new SignUpResponseDto(token, newUser.getName()));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponseDto> signIn(@RequestBody SignInRequestDto body) {
        User user = this.userRepository.findUserByEmail(body.email()).orElseThrow(()-> new RuntimeException("Email Repository Or Password Invalid"));

        if(!this.passwordEncoder.matches(body.password(), user.getPassword())) {
            throw new RuntimeException("Email Or Password Invalid");
        }

        String token = this.tokenService.generateToken(user);
        return ResponseEntity.ok(new SignInResponseDto(token, user.getName()));
    }

    @GetMapping("test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Success");
    }

}
