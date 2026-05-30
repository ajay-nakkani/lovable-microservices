package com.ajay.lovable.intelligenceservice.service.impl;


import com.ajay.lovable.commonlib.dto.PlanDto;
import com.ajay.lovable.commonlib.security.AuthUtil;
import com.ajay.lovable.intelligenceservice.client.AccountClient;
import com.ajay.lovable.intelligenceservice.entity.UsageLog;
import com.ajay.lovable.intelligenceservice.repository.UsageLogRepository;
import com.ajay.lovable.intelligenceservice.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

    private final UsageLogRepository usageLogRepository;
    private final AuthUtil authUtil;
    private final AccountClient accountClient;


    @Override
    public void recordTokenUsage(Long userId, int actualTokens) {
        LocalDate today = LocalDate.now();

        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId, today).
                                              orElseGet(() -> createNewDailyLog(userId, today));

        todayLog.setTokensUsed(todayLog.getTokensUsed() + actualTokens);
        usageLogRepository.save(todayLog);
    }

    @Override
    public void checkDailyTokensUsage() {
        Long userId = authUtil.getCurrentUserId();

        PlanDto plan = accountClient.getCurrentSubscribedPlanByUser();


        LocalDate today = LocalDate.now();

        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId, today).
                                              orElseGet(() -> createNewDailyLog(userId, today));

        if(plan.unlimitedAi()) return;

        int currentUsage = todayLog.getTokensUsed();
        int limit = plan.maxTokensPerDay();

        if (currentUsage >= limit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Daily limit reached, Upgrade now");
        }

    }

    private UsageLog createNewDailyLog(Long userId, LocalDate date) {
        UsageLog newLog = UsageLog.builder()
                                  .userId(userId)
                                  .date(date)
                                  .tokensUsed(0)
                                  .build();
        return usageLogRepository.save(newLog);
    }
}
