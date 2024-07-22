package com.example.demo.user.controller.response;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {
    @Test
    public void User으로_응답을_생성할_수_있다(){
        //given
        User user = User.builder()
                .id(1L)
                .email("hanseu9839@gmail.com")
                .nickname("hanseu9839")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .build();

        //when
        UserResponse userResponse = UserResponse.from(user);

        //then
        assertThat(userResponse.getId()).isEqualTo(1);
        assertThat(userResponse.getEmail()).isEqualTo("hanseu9839@gmail.com");
        assertThat(userResponse.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(userResponse.getLastLoginAt()).isEqualTo(100L);
    }
}