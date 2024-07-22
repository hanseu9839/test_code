package com.example.demo.user.service;

import com.example.demo.mock.FakeMailSender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CertificationServiceTest {
    @Test
    public void 이메일과_컨텐츠가_제대로_만들어져서_보내는지_테스트한다() {
        //given
        FakeMailSender fakeMailSender = new FakeMailSender();
        CertificationService certificationService = new CertificationService(fakeMailSender);
        certificationService.send("hanseu9839@gmail.com", 1, "aaaaaaa-aaaaa-aaaaaaa-aaaaa-aaaab");

        // then
        assertThat(fakeMailSender.email).isEqualTo("hanseu9839@gmail.com");
        assertThat(fakeMailSender.title).isEqualTo("Please certify your email address");
        assertThat(fakeMailSender.content).isEqualTo("Please click the following link to certify your email address: http://localhost:8080/api/users/1/verify?certificationCode=aaaaaaa-aaaaa-aaaaaaa-aaaaa-aaaab");
    }

}
