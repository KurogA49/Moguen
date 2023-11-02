package com.mogun.backend.mogun.service;

import com.mogun.backend.domain.user.User;
import com.mogun.backend.domain.user.repository.UserRepository;
import com.mogun.backend.domain.userDetail.repository.UserDetailRepository;
import com.mogun.backend.service.user.UserService;
import com.mogun.backend.service.user.dto.UserDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    // Test 주체
    private UserService userService;

    // Test 협력자
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserDetailRepository userDetailRepository;

    // Test 실행 전 userService 에 가짜 객체를 주입
    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userDetailRepository);
    }

    @Test
    @DisplayName("회원 등록 테스트")
    void joinMember() {
		/*
		given
		 */
        String email = "didrjsgh11@naver.com";
        String password = "175863";
        char gender = 'm';
        String name = "무근이";
		/*
		when
		 */
        userRepository.save(UserDto.builder()
                .email(email)
                .password(password)
                .gender(gender)
                .name(name)
                .height(173)
                .weight(66)
                .muscleMass(13.8F)
                .bodyFat(17.3F)
                .build().toEntity());
		/*
		then
		 */
        Optional<User> user = userRepository.findByEmail(email);
        Assertions.assertThat(user.isEmpty()).isEqualTo(false);
    }
}
