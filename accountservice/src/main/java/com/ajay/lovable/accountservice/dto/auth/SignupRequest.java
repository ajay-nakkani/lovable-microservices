package com.ajay.lovable.accountservice.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(@Email @NotBlank String username, @NotBlank String name,@Size(min = 3,max = 15) String password) {
}
