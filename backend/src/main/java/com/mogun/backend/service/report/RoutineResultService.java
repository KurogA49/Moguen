package com.mogun.backend.service.report;

import com.mogun.backend.domain.exercise.Exercise;
import com.mogun.backend.domain.report.muscleActInSet.MuscleActInSetLog;
import com.mogun.backend.domain.report.muscleActInSet.repository.MuscleActInSetLogRepository;
import com.mogun.backend.domain.report.routineReport.RoutineReport;
import com.mogun.backend.domain.report.routineReport.repository.RoutineReportRepository;
import com.mogun.backend.domain.report.routineResult.RoutineResult;
import com.mogun.backend.domain.report.routineResult.repository.RoutineResultRepository;
import com.mogun.backend.domain.report.setReport.SetReport;
import com.mogun.backend.domain.report.setReport.repository.SetReportRepository;
import com.mogun.backend.domain.report.usedMusclePart.repository.UsedMusclePartRepository;
import com.mogun.backend.domain.user.User;
import com.mogun.backend.domain.user.repository.UserRepository;
import com.mogun.backend.service.attachPart.AttachPartService;
import com.mogun.backend.service.report.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoutineResultService {

    private final UserRepository userRepository;
    private final RoutineReportRepository reportRepository;
    private final RoutineResultRepository resultRepository;
    private final SetReportRepository setReportRepository;
    private final AttachPartService attachPartService;
    private final MuscleActInSetLogRepository actInSetLogRepository;
    private final UsedMusclePartRepository usedMusclePartRepository;

    public String createResult(ResultDto dto) {

        Optional<RoutineReport> report = reportRepository.findById(dto.getReportKey());
        if(report.isEmpty())
            return "요청 오류: 등록된 적 없는 루틴 기록";

        Optional<RoutineResult> result = resultRepository.findByRoutineReport(report.get());
        if(result.isPresent())
            return "요청 오류: 기록된 루틴 결과에 대한 재작성";

        resultRepository.save(dto.toRoutineResultEntity(report.get()));


        return "SUCCESS";
    }

    public SummaryResultDto getAllInfoOfResult(ResultDto dto) {

        Optional<RoutineResult> result = resultRepository.findById(dto.getResultKey());
        if(result.isEmpty())
            return SummaryResultDto.builder()
                    .performTime((long) -1)
                    .build();

        Optional<User> user = userRepository.findByEmail(dto.getUserEmail());
        if(user.isEmpty())
            return SummaryResultDto.builder()
                    .performTime((long) -2)
                    .build();


        if(!result.get().getUser().equals(user.get()))
            return SummaryResultDto.builder()
                    .performTime((long) -3)
                    .build();

        RoutineReport report = result.get().getRoutineReport();
        List<SetReport> setReportList = setReportRepository.findAllByRoutineReport(report);
        List<ExerciseResultDto> exerciseResultDtoList = new ArrayList<>();

        for (SetReport setReport: setReportList) {
            if(exerciseResultDtoList.isEmpty()) {
                exerciseResultDtoList.add(ExerciseResultDto.builder()
                        .execName(setReport.getExercise().getName())
                        .imagePath(setReport.getExercise().getImagePath())
                        .sets(0)
                        .partList(new ArrayList<>())
                        .build());

                exerciseResultDtoList.get(0).getPartList().addAll(attachPartService.getAllPartNameByExercise(setReport.getExercise()));
            }

            int lastIndex = exerciseResultDtoList.size() - 1;
            Exercise exec = setReport.getExercise();

            if(!exec.equals(setReportList.get(lastIndex).getExercise())) {
                exerciseResultDtoList.add(ExerciseResultDto.builder()
                        .execName(setReport.getExercise().getName())
                        .imagePath(setReport.getExercise().getImagePath())
                        .sets(1)
                        .partList(new ArrayList<>())
                        .build());

                lastIndex = exerciseResultDtoList.size() - 1;
                exerciseResultDtoList.get(lastIndex).getPartList().addAll(attachPartService.getAllPartNameByExercise(setReport.getExercise()));
            } else {
                int lastSet = exerciseResultDtoList.get(lastIndex).getSets();
                exerciseResultDtoList.get(lastIndex).setSets(lastSet + 1);
            }
        }

        Duration performTime = Duration.between(result.get().getRoutineReport().getStartTime(), result.get().getRoutineReport().getEndTime());

        return SummaryResultDto.builder()
                .routineName(result.get().getRoutineReport().getRoutineName())
                .routineDate(result.get().getRoutineDate())
                .consumeCalorie(result.get().getConsumeCalorie())
                .performTime(performTime.toMinutes())
                .exerciseResultDtoList(exerciseResultDtoList)
                .build();
    }

    public List<SetResultListDto> getExerciseResult(ResultDto dto) {

        List<SetResultListDto> list = new ArrayList<>();
        Optional<User> user = userRepository.findByEmail(dto.getUserEmail());
        if(user.isEmpty()) {
            list.add(SetResultListDto.builder()
                    .status(-1)
                    .build());
            return list;
        }

        Optional<RoutineResult> result = resultRepository.findById(dto.getResultKey());
        if(result.isEmpty()) {
            list.add(SetResultListDto.builder()
                    .status(-2)
                    .build());
            return list;
        }

        if(!user.get().equals(result.get().getUser())) {
            list.add(SetResultListDto.builder()
                    .status(-3)
                    .build());
            return list;
        }

        List<SetReport> setReportList = setReportRepository.findAllByRoutineReport(result.get().getRoutineReport());

        for(SetReport report: setReportList) {
            if(list.isEmpty()) {
                list.add(SetResultListDto.builder()
                        .exec(report.getExercise())
                        .status(0)
                        .setResultDtoList(new ArrayList<>())
                        .build());
            }

            int lastIndex = list.size() - 1;
            Exercise exec = report.getExercise();

            if(!exec.equals(list.get(lastIndex).getExec())) {
                list.add(SetResultListDto.builder()
                        .exec(report.getExercise())
                        .setResultDtoList(new ArrayList<>())
                        .build());
            } else {
                List<MuscleActInSetLog> logList = actInSetLogRepository.findAllBySetReport(report);
                List<Float> activity = new ArrayList<>();
                for (MuscleActInSetLog log: logList)
                    activity.add(log.getMuscleActivity());

                list.get(lastIndex).getSetResultDtoList().add(
                        SetResultDto.builder()
                                .setNumber(report.getSetNumber())
                                .weight(report.getTrainWeight())
                                .targetRep(report.getTargetRep())
                                .successRep(report.getSuccessesRep())
                                .muscleActivityList(activity)
                                .build()
                );
            }
        }

        return list;
    }

    public List<ResultListDto> getMonthlyResult(ResultDto dto) {

        List<ResultListDto> list = new ArrayList<>();
        Optional<User> user = userRepository.findById(dto.getUserKey());
        if(user.isEmpty()) {
            list.add(ResultListDto.builder().routineCount(-1).build());
            return list;
        }

        LocalDate lastDate = LocalDate.now().minusMonths(1);


        List<RoutineResult> results = resultRepository.findAllByFromRoutineDateAndUser(lastDate, user.get());

        for(RoutineResult result: results) {
            if(list.isEmpty()) {
                list.add(ResultListDto.builder()
                        .date(result.getRoutineDate())
                        .routineCount(1)
                        .routineReports(new ArrayList<>())
                        .build());
            }

            int lastIndex = list.size() - 1;
            RoutineReport report = result.getRoutineReport();

            if(list.get(lastIndex).getDate().isEqual(result.getRoutineDate())) {
                list.get(lastIndex).getRoutineReports().add(SimpleReportInfo.builder()
                        .routineName(report.getRoutineName())
                        .reportKey(report.getRoutineReportKey())
                        .startTime(report.getStartTime())
                        .endTime(report.getEndTime())
                        .build());
            } else {
                list.add(ResultListDto.builder()
                        .date(result.getRoutineDate())
                        .routineCount(1)
                        .routineReports(new ArrayList<>())
                        .build());
                lastIndex = list.size() - 1;
                list.get(lastIndex).getRoutineReports().add(SimpleReportInfo.builder()
                        .routineName(report.getRoutineName())
                        .reportKey(report.getRoutineReportKey())
                        .startTime(report.getStartTime())
                        .endTime(report.getEndTime())
                        .build());
            }
        }

        return list;
    }
}
