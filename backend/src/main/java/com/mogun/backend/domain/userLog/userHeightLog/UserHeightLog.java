package com.mogun.backend.domain.userLog.userHeightLog;

import com.mogun.backend.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UserHeightLog {

    @Id
    @Column(name = "log_key")
    private Long logKey;

    @ManyToOne
    @JoinColumn(name = "user_key")
    private User user;

    @Column(name = "height_before")
    private float heightBefore;

    @Column(name = "height_after")
    private float heightAfter;

    @Column(name = "changed_time")
    private LocalDateTime changedTime;
}
