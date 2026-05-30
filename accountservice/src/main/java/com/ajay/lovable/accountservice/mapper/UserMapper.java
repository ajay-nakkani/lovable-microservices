package com.ajay.lovable.accountservice.mapper;


import com.ajay.lovable.accountservice.dto.auth.SignupRequest;
import com.ajay.lovable.accountservice.dto.auth.UserProfileResponse;
import com.ajay.lovable.accountservice.entity.User;

import com.ajay.lovable.commonlib.dto.UserDto;
import com.ajay.lovable.commonlib.security.JwtUserPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUserFromSignUpRequest(SignupRequest signupRequest);

    @Mapping(source = "userId", target= "id")
    UserProfileResponse toUserProfileResponse(JwtUserPrincipal user);

    UserDto toUserDto(User user);

}
