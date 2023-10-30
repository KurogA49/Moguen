package com.mogun.backend.domain.report.routineReport;

import com.mogun.backend.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineReport {

    @Id
    @GeneratedValue
    @Column(name = "log_key")
    private Long routineReportKey;

    @ManyToOne
    @JoinColumn(name = "user_key")
    private User user;

    @Column(name = "routine_name")
    private String routineName;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "is_attached")
    private char isAttached;
}
