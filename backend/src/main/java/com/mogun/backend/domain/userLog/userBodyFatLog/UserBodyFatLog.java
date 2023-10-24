package com.mogun.backend.domain.userLog.userBodyFatLog;

import com.mogun.backend.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UserBodyFatLog {

    @Id
    @Column(name = "log_key")
    private Long logKey;

    @ManyToOne
    @JoinColumn(name = "user_key")
    private User user;

    @Column(name = "body_fat_before")
    private float bodyFatBefore;

    @Column(name = "body_fat_after")
    private float bodyFatAfter;

    @Column(name = "changed_time")
    private LocalDateTime changedTime;
}