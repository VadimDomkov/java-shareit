package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentMapperTest {
    @Autowired
    CommentMapper commentMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void dtoToComment() {
        assertNull(commentMapper.dtoToComment(null));
    }

    @Test
    void commentToDto() {
        assertNull(commentMapper.commentToDto(null));
    }

    @Test
    void commentToDto_withAuthor() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .build();

        Comment comment = new Comment();
        comment.setAuthor(user);

        CommentDto result = commentMapper.commentToDto(comment);

        assertEquals(user.getName(), result.getAuthorName());
    }

    @Test
    void commentToDto_withAuthorWithoutName() {
        User user = User.builder()
                .id(1L)
                .build();

        Comment comment = new Comment();
        comment.setAuthor(user);

        assertNull(commentMapper.commentToDto(comment).getAuthorName());
    }
}