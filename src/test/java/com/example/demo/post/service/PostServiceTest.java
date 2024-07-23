package com.example.demo.post.service;

import com.example.demo.mock.*;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.CertificationService;
import com.example.demo.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;

class PostServiceTest {

    private PostService postService;

    @BeforeEach
    void init(){
        FakePostRepository fakePostRepository = new FakePostRepository();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();

        this.postService = PostService.builder()
                .userRepository(fakeUserRepository)
                .postRepository(fakePostRepository)
                .clockHolder(new TestClockHolder(16795307673958L))
                .build();

        User user1 = User.builder()
                .id(1L)
                .email("hanseu9839@gmail.com")
                .nickname("hanseu9839")
                .address("Seoul")
                .certificationCode("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("hanseu98391@gmail.com")
                .nickname("hanseu98391")
                .address("Incheon")
                .certificationCode("aaaaaaaaaaaa-aaaaaaa-aaaaaaaaaab")
                .status(UserStatus.PENDING)
                .lastLoginAt(0L)
                .build();

        fakeUserRepository.save(user1);
        fakeUserRepository.save(user2);

        fakePostRepository.save(Post.builder()
                .id(1L)
                .content("helloworld")
                .createdAt(1678530673958L)
                .modifiedAt(0L)
                .writer(user1)
                .build());
    }

    @Test
    void getById는_존재하는_게시물을_내려준다() {
        //given
        //when
        Post result = postService.getById(1);

        //then
        assertThat(result.getContent()).isEqualTo("helloworld");
        assertThat(result.getWriter().getEmail()).isEqualTo("hanseu9839@gmail.com");
    }

    @Test
    void postCreateDto_를_이용하여_유저를_생성할_수_있다() {
        // given
        PostCreate userCreateDto = PostCreate.builder()
                .writerId(1)
                .content("foobar")
                .build();

        // when
        Post result = postService.create(userCreateDto);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getContent()).isEqualTo("foobar");
        assertThat(result.getCreatedAt()).isEqualTo(16795307673958L);


    }

    @Test
    void postUpdateDto_를_이용하여_유저를_생성할_수_있다() {
        // given
        PostUpdate userUpdateDto = PostUpdate.builder()
                .content("hello world :)")
                .build();

        // when
        postService.update(1, userUpdateDto);

        // then
        Post post = postService.getById(1L);
        assertThat(post.getId()).isNotNull();
        assertThat(post.getContent()).isEqualTo("hello world :)");
        assertThat(post.getModifiedAt()).isEqualTo(16795307673958L);
//         assertThat(result.getCertificationCode()).isEqualTo("T.T"); // FIXME
    }
}