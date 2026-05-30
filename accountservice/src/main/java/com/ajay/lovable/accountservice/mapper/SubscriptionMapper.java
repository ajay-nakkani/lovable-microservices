package com.ajay.lovable.accountservice.mapper;

import com.ajay.lovable.accountservice.dto.subscription.SubscriptionResponse;
import com.ajay.lovable.accountservice.entity.Plan;
import com.ajay.lovable.accountservice.entity.Subscription;
import com.ajay.lovable.commonlib.dto.PlanDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanDto toPlanResponse(Plan plan);
}
