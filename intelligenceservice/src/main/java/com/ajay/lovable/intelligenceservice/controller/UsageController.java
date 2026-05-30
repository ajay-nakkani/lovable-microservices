package com.ajay.lovable.intelligenceservice.controller;


import com.ajay.lovable.intelligenceservice.dto.usage.UsageTodayResponse;
import com.ajay.lovable.intelligenceservice.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
public class UsageController {

    private final UsageService usageService;

    @GetMapping("/today")
    public ResponseEntity<UsageTodayResponse> getTodayUsage()
    {
        Long userId = 1L;
//        return ResponseEntity.ok(usageService.getTodayUsageOfUser(userId));
        return null;
    }

//    @GetMapping("/limits")
//    public ResponseEntity<PlanLimitResponse> getPlanLimits()
//    {
//        Long userId = 1L;
//        return ResponseEntity.ok(usageService.getCurrentSubscriptionLimitsOfUser(userId));
//    }

}
