package com.ajay.lovable.intelligenceservice.service;


public interface UsageService {

     void recordTokenUsage(Long userId, int actualTokens);
     void checkDailyTokensUsage();
}
