package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;


public class UserServiceTest {

    private UserServiceImpl userService;

    @BeforeEach
    void init(){
        FakeMailSender fakeMailSender = new FakeMailSender();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();

        this.userService = UserServiceImpl.builder()
                .clockHolder(new TestClockHolder(1678530673958L))
                .certificationService(new CertificationService(fakeMailSender))
                .uuidHolder(new TestUuidHolder("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaab"))
                .userRepository(fakeUserRepository)
                .build();

        fakeUserRepository.save(User.builder()
                .id(1L)
                .email("hanseu9839@gmail.com")
                .nickname("hanseu9839")
                .address("Seoul")
                .certificationCode("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build());

        fakeUserRepository.save(User.builder()
                .id(2L)
                .email("hanseu983933@gmail.com")
                .nickname("hanseu983933")
                .address("Incheon")
                .certificationCode("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaab")
                .status(UserStatus.PENDING)
                .lastLoginAt(0L)
                .build());
    }
    @Test
    void getByEmail은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        String email = "hanseu9839@gmail.com";
        User result = userService.getByEmail(email);

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
           userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);

    }


    @Test
    void getById은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        // when
        User result = userService.getById(1L);
        // then
        assertThat(result.getNickname()).isEqualTo("hanseu9839");
    }


    @Test
    void getById은_PENDING_상태인_유저를_찾아올_수_없다() {
        // given
        // when
        // then
        assertThatThrownBy(() ->{
                userService.getById(2L);
        }).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void userCreateDto_를_이용하여_유저를_생성할_수_있다() {
        // given
        UserCreate userCreate = UserCreate.builder()
                .email("hans9839@naver.com")
                .address("Bucheon")
                .nickname("hanseu9839-h")
                .build();

        // when
        User result = userService.create(userCreate);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getCertificationCode()).isEqualTo("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaab"); //FIXME

    }

    @Test
    void userUpdateDto_를_이용하여_유저를_생성할_수_있다() {
        // given
        UserUpdate userUpdate = UserUpdate.builder()
                .address("Incheon")
                .nickname("hanseu9839-nn")
                .build();

        // when
        userService.update(1, userUpdate);

        // then
        User user = userService.getById(1L);
        assertThat(user.getId()).isNotNull();
        assertThat(user.getAddress()).isEqualTo("Incheon");
        assertThat(user.getNickname()).isEqualTo("hanseu9839-nn");
        // assertThat(result.getCertificationCode()).isEqualTo("T.T"); // FIXME
    }


    @Test
    void user를_로그인_시키면_마지막_로그인_시간이_변경된다() {
        // given
        // when
        userService.login(1L);

        // then
        User user = userService.getById(1);
        assertThat(user.getLastLoginAt()).isEqualTo(1678530673958L);
    }

    @Test
    void PENDING_상태의_사용자는_인증_코드로_사용할_수_있다() {
        // given
        // when
        userService.verifyEmail(2L, "aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaab");

        // then
        User user = userService.getById(2L);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }


    @Test
    void PENDING_상태의_사용자는_잘못된_인증인증_코드를_받으면_에러를_던진다() {
        // given
        // when
        // then
        assertThatThrownBy(() -> {
            userService.verifyEmail(2, "aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaabc");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);

    }
}
