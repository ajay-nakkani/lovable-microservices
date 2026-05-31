package com.ajay.lovable.workspace.client;

import com.ajay.lovable.commonlib.dto.PlanDto;
import com.ajay.lovable.commonlib.dto.UserDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name = "accountservice", path = "/account", url="${ACCOUNT_SERVICE_URI:}")
public interface AccountClient {

    @GetMapping("/internal/v1/users/by-email")
    Optional<UserDto> getUserByEmail(@RequestParam("email") String email);

    @GetMapping("/internal/v1/billing/current-plan")
    PlanDto getCurrentSubscribedPlanByUser();
}
