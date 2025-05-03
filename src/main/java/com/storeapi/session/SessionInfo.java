package com.storeapi.session;

import com.storeapi.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class SessionInfo {
    private User user;
    private LocalDateTime createdAt;
}
