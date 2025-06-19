package com.czar.first_java_spring_project.dto.request;

public record SignUpRequestDto(String name, String email, String password, Boolean termsAndCondictionsAccepted, Boolean newsLetterSubscriber) {
}
