package ru.practicum.shareit.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import ru.practicum.shareit.dto.booking.RequestBookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestBookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeRequestBookingDto() throws Exception {
        RequestBookingDto dto = new RequestBookingDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.of(2026, 6, 20, 10, 0, 0));
        dto.setEnd(LocalDateTime.of(2026, 6, 25, 10, 0, 0));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"itemId\":10");
        assertThat(json).contains("\"start\":\"2026-06-20T10:00:00\"");
        assertThat(json).contains("\"end\":\"2026-06-25T10:00:00\"");
    }

    @Test
    void shouldDeserializeRequestBookingDto() throws Exception {
        String json = """
            {
                "itemId": 10,
                "start": "2026-06-20T10:00:00",
                "end": "2026-06-25T10:00:00"
            }
            """;

        RequestBookingDto dto = objectMapper.readValue(json, RequestBookingDto.class);

        assertThat(dto.getItemId()).isEqualTo(10L);
        assertThat(dto.getStart()).isEqualTo(
            LocalDateTime.of(2026, 6, 20, 10, 0, 0)
        );
        assertThat(dto.getEnd()).isEqualTo(
            LocalDateTime.of(2026, 6, 25, 10, 0, 0)
        );
    }
}