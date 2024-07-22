package com.example.demo.user.domain;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserTest {

    @Test
    public void User는_UserCreate_객체로_생성할_수_있다(){
        //given
        UserCreate userCreate = UserCreate.builder()
                .email("hanseu9839@gmail.com")
                .nickname("hanseu9839")
                .address("Pangvo")
                .build();

        //when
        User user = User.from(userCreate, new TestUuidHolder("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaa"));

        //then
        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo("hanseu9839@gmail.com");
        assertThat(user.getNickname()).isEqualTo("hanseu9839");
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getCertificationCode()).isEqualTo("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaa");
    }

    @Test
    public void UserUpdate_객체로_데이터를_업데이트_할_수_있다(){
        User user = User.builder()
                .id(1L)
                .email("hanseu9839@gmail.com")
                .nickname("hanseu9839")
                .address("Seoul")
                .certificationCode("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaab")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .build();

        //given
        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("hanseu9839-h")
                .address("cheonan")
                .build();

        //when
        user = user.update(userUpdate);

        //then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("hanseu9839@gmail.com");
        assertThat(user.getNickname()).isEqualTo("hanseu9839-h");
        assertThat(user.getAddress()).isEqualTo("cheonan");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getCertificationCode()).isEqualTo("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaab");
    }

    @Test
    public void 로그인을_할_수_있고_로그인시_마지막_로그인_시간이_변경된다(){
        //given
        User user = User.builder()
                .id(1L)
                .email("hanseu9839@gmail.com")
                .nickname("hanseu9839")
                .address("Seoul")
                .certificationCode("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaab")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .build();

        //when
        user = user.login(new TestClockHolder(1678530673958L));
        //then
        assertThat(user.getLastLoginAt()).isEqualTo(1678530673958L);
    }

    @Test
    public void 유효한_인증_코드로_계정을_활성화_할_수_있다(){
        //given
        User user = User.builder()
                .id(1L)
                .email("hanseu9839@gmail.com")
                .nickname("hanseu9839")
                .address("Seoul")
                .certificationCode("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaab")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .build();

        //when

        user = user.certificate("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaab");
        //then

        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    public void 잘못된_인증_코드로_계정을_활성화_하려하면_에러를_던진다(){
        //given

        User user = User.builder()
                .id(1L)
                .email("hanseu9839@gmail.com")
                .nickname("hanseu9839")
                .address("Seoul")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaa")
                .build();
        //when
        //then
        assertThatThrownBy(()-> {user.certificate("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaab");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
