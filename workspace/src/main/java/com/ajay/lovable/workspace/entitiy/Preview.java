package com.ajay.lovable.workspace.entitiy;


import com.ajay.lovable.commonlib.enums.PreviewStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;



@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Preview {
    Long id;

    Project project;

    String namespace;//isolated resources

    String podName;
    String previewUrl;

    PreviewStatus status;


    Instant startedAt;
    Instant terminatedAt;
    Instant createdAt;


}
