package com.ajay.lovable.intelligenceservice.entity;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class ChatSessionId implements Serializable {

    Long projectId;
    Long userId;

}