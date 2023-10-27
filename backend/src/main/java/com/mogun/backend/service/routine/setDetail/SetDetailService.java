package com.mogun.backend.service.routine.setDetail;

import com.mogun.backend.domain.routine.setDetail.SetDetail;
import com.mogun.backend.domain.routine.setDetail.repository.SetDetailRepository;
import com.mogun.backend.domain.routine.userRoutine.UserRoutine;
import com.mogun.backend.domain.routine.userRoutine.repository.UserRoutineRepository;
import com.mogun.backend.domain.routine.userRoutinePlan.UserRoutinePlan;
import com.mogun.backend.domain.routine.userRoutinePlan.repository.UserRoutinePlanRepository;
import com.mogun.backend.domain.user.User;
import com.mogun.backend.domain.user.repository.UserRepository;
import com.mogun.backend.service.routine.dto.RoutineDto;
import com.mogun.backend.service.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SetDetailService {

    private final SetDetailRepository setDetailRepository;
    private final UserRepository userRepository;
    private final UserRoutineRepository routineRepository;
    private final UserRoutinePlanRepository planRepository;

    public String addOneSetGoal(RoutineDto dto) {

        Optional<UserRoutinePlan> plan = planRepository.findById(dto.getPlanKey());
        if(plan.isEmpty())
            return "요청 오류: 등록된 운동이 아님";

        UserRoutine routine = plan.get().getUserRoutine();
        User user = plan.get().getUser();

        setDetailRepository.save(dto.toSetDetailEntity(user, routine, plan.get()));

        return "SUCCESS";
    }

    public String addAllSetGoal(List<RoutineDto> dtoList) {

        Optional<UserRoutinePlan> plan = planRepository.findById(dtoList.get(0).getPlanKey());
        if(plan.isEmpty())
            return "요청 오류: 등록된 운동이 아님";

        for(RoutineDto dto: dtoList) {

            UserRoutine routine = plan.get().getUserRoutine();
            User user = plan.get().getUser();

            setDetailRepository.save(dto.toSetDetailEntity(user, routine, plan.get()));
        }

        return  "SUCCESS";
    }

    public List<SetDetail> getAllSetInfo(RoutineDto dto) {

        Optional<UserRoutinePlan> plan = planRepository.findById(dto.getPlanKey());

        return setDetailRepository.findAllByUserRoutinePlan(plan.get());
    }

    public String deleteOneSet(RoutineDto dto) {

        setDetailRepository.deleteById(dto.getSetKey());

        return "SUCCESS";
    }

    public String deleteAllSet(RoutineDto dto) {

        Optional<UserRoutinePlan> plan = planRepository.findById(dto.getPlanKey());
        if(plan.isEmpty())
            return "요청 오류: 등록된 운동이 아님";

        setDetailRepository.deleteAllByUserRoutinePlan(plan.get());

        return "SUCCESS";
    }
}
