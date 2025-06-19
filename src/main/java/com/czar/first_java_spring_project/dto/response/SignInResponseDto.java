package com.czar.first_java_spring_project.dto.response;

public record SignInResponseDto(String accessToken, String refreshToken, String name) {
}
