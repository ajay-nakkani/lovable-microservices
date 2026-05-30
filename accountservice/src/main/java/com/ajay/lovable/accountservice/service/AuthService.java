package com.ajay.lovable.accountservice.service;


import com.ajay.lovable.accountservice.dto.auth.AuthResponse;
import com.ajay.lovable.accountservice.dto.auth.LoginRequest;
import com.ajay.lovable.accountservice.dto.auth.SignupRequest;

public interface AuthService {
    public AuthResponse signup(SignupRequest request) ;

    public  AuthResponse login(LoginRequest request) ;
}
