package com.example.demo.service;

import com.example.demo.exception.CertificationCodeNotMatchedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserCreateDto;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void getByEmail은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        String email = "hanseu9839@gmail.com";
        UserEntity result = userService.getByEmail(email);

        // when
        assertThat(result.getNickname()).isEqualTo("hanseu9839");


        // then
    }


    @Test
    void getByEmail은_PENDING_상태인_유저를_찾아올_수_없다() {
        // given
        String email = "hanseu98391@gmail.com";


        // when
        // then
        assertThatThrownBy(() ->{
            UserEntity result = userService.getByEmail(email);

        }).isInstanceOf(ResourceNotFoundException.class);

    }


    @Test
    void getById은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        // when
        UserEntity result = userService.getById(11L);
        // then
        assertThat(result.getNickname()).isEqualTo("hanseu9839");
    }


    @Test
    void getById은_PENDING_상태인_유저를_찾아올_수_없다() {
        // given
        // when
        // then
        assertThatThrownBy(() ->{
            UserEntity result = userService.getById(12L);

        }).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void userCreateDto_를_이용하여_유저를_생성할_수_있다() {
        // given
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .email("hans9839@naver.com")
                .address("Bucheon")
                .nickname("hanseu9839-h")
                .build();

        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when
        UserEntity result = userService.create(userCreateDto);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        // assertThat(result.getCertificationCode()).isEqualTo("T.T"); //FIXME

    }

    @Test
    void userUpdateDto_를_이용하여_유저를_생성할_수_있다() {
        // given
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .address("Incheon")
                .nickname("hanseu9839-nn")
                .build();

        // when
        userService.update(11, userUpdateDto);

        // then
        UserEntity userEntity = userService.getById(11L);
        assertThat(userEntity.getId()).isNotNull();
        assertThat(userEntity.getAddress()).isEqualTo("Incheon");
        assertThat(userEntity.getNickname()).isEqualTo("hanseu9839-nn");
        // assertThat(result.getCertificationCode()).isEqualTo("T.T"); // FIXME
    }


    @Test
    void user를_로그인_시키면_마지막_로그인_시간이_변경된다() {
        // given
        // when
        userService.login(11);

        // then
        UserEntity userEntity = userService.getById(11);
        assertThat(userEntity.getLastLoginAt()).isGreaterThan(0L);
        // assertThat(result.getLastLoginAt()).isEqualTo("T.T"); // FIXME
    }

    @Test
    void PENDING_상태의_사용자는_인증_코드로_사용할_수_있다() {
        // given
        // when
        userService.verifyEmail(12, "aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaab");

        // then
        UserEntity userEntity = userService.getById(12);
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }


    @Test
    void PENDING_상태의_사용자는_잘못된_인증인증_코드를_받으면_에러를_던진다() {
        // given
        // when
        // then
        assertThatThrownBy(() -> {
            userService.verifyEmail(12, "aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaac");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);

    }
}
