package ru.practicum.shareit.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import ru.practicum.shareit.dto.item.CommentResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentResponseDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeCommentResponseDto() throws Exception {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(1L);
        dto.setText("Отличная вещь!");
        dto.setAuthorName("Иван");
        dto.setCreated(LocalDateTime.of(2026, 6, 19, 20, 0, 0));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Отличная вещь!\"");
        assertThat(json).contains("\"authorName\":\"Иван\"");
        assertThat(json).contains("\"created\":\"2026-06-19T20:00:00\"");
    }

    @Test
    void shouldDeserializeCommentResponseDto() throws Exception {
        String json = "{\"id\": 1, "
            + "\"text\": \"Отличная вещь!\", "
            + "\"authorName\": \"Иван\", "
            + "\"created\": \"2026-06-19T20:00:00\"}";

        CommentResponseDto dto = objectMapper.readValue(json, CommentResponseDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Отличная вещь!");
        assertThat(dto.getAuthorName()).isEqualTo("Иван");
        assertThat(dto.getCreated()).isEqualTo(
            LocalDateTime.of(2026, 6, 19, 20, 0, 0)
        );
    }
}