package com.ajay.lovable.workspace.entitiy;


import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectMemberId {

    Long projectId;
    Long userId;

}
