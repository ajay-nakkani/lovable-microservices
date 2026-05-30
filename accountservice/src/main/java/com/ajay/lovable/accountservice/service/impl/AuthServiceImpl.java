package com.ajay.lovable.accountservice.service.impl;

import com.ajay.lovable.accountservice.dto.auth.AuthResponse;
import com.ajay.lovable.accountservice.dto.auth.LoginRequest;
import com.ajay.lovable.accountservice.dto.auth.SignupRequest;
import com.ajay.lovable.accountservice.entity.User;
import com.ajay.lovable.accountservice.mapper.UserMapper;
import com.ajay.lovable.accountservice.repository.UserRepository;
import com.ajay.lovable.accountservice.service.AuthService;
import com.ajay.lovable.commonlib.error.BadRequestException;
import com.ajay.lovable.commonlib.security.AuthUtil;
import com.ajay.lovable.commonlib.security.JwtUserPrincipal;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthUtil authUtil;
    AuthenticationManager authenticationManager;

    @Override
    public AuthResponse signup(SignupRequest request) {
        userRepository.findByUsername(request.username()).ifPresent((err)-> {
            throw new BadRequestException("username already exists in the system");
        });

        User user = userMapper.toUserFromSignUpRequest(request);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user = userRepository.save(user);
        JwtUserPrincipal jwtUserPrincipal = new JwtUserPrincipal(user.getId(), user.getName(),
                user.getUsername(), null,  new ArrayList<>());

        String token = authUtil.generateToken(jwtUserPrincipal);
        return new AuthResponse(token, userMapper.toUserProfileResponse(jwtUserPrincipal));

    }

    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(),request.password())
        );
        JwtUserPrincipal user = (JwtUserPrincipal) authentication.getPrincipal();
        String token = authUtil.generateToken(user);

        return new AuthResponse(token, userMapper.toUserProfileResponse(user));

    }
}
