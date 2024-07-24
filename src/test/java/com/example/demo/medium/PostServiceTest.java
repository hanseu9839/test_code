package com.example.demo.medium;

import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/post-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostServiceTest {

    @Autowired
    private PostServiceImpl postService;

    @Test
    void getById는_존재하는_게시물을_내려준다() {
        //given
        //when
        Post result = postService.getById(11);

        //then
        assertThat(result.getContent()).isEqualTo("helloworld");
        assertThat(result.getWriter().getEmail()).isEqualTo("hanseu9839@gmail.com");
    }

    @Test
    void postCreateDto_를_이용하여_유저를_생성할_수_있다() {
        // given
        PostCreate userCreateDto = PostCreate.builder()
                .writerId(11)
                .content("foobar")
                .build();

        // when
        Post result = postService.create(userCreateDto);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getContent()).isEqualTo("foobar");
        assertThat(result.getCreatedAt()).isGreaterThan(0);


    }

    @Test
    void postUpdateDto_를_이용하여_유저를_생성할_수_있다() {
        // given
        PostUpdate userUpdateDto = PostUpdate.builder()
                .content("hello world :)")
                .build();

        // when
        postService.update(11, userUpdateDto);

        // then
        Post post = postService.getById(11L);
        assertThat(post.getId()).isNotNull();
        assertThat(post.getContent()).isEqualTo("hello world :)");
        assertThat(post.getModifiedAt()).isGreaterThan(0);
        // assertThat(result.getCertificationCode()).isEqualTo("T.T"); // FIXME
    }
}