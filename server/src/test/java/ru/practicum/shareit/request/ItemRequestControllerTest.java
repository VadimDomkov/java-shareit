package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    ItemRequestService itemRequestService;

    @InjectMocks
    ItemRequestController itemRequestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemRequestDto dto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        dto = new ItemRequestDto();
        dto.setDescription("desc");
    }

    @Test
    @SneakyThrows
    void createRequest_whenInvoke_shouldReturnOkWithResponseEntityEqualsToRequestEntity() {
        when(itemRequestService.createRequest(any(), anyLong()))
                .thenReturn(dto);

        mvc.perform(post("/requests")
                .content(objectMapper.writeValueAsString(dto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(dto.getDescription())));
    }

//    @Test
//    @SneakyThrows
//    void createRequest_shouldReturn400WhenDescIsEmpty() {
//        dto.setDescription(null);
//        mvc.perform(post("/requests")
//                .content(objectMapper.writeValueAsString(dto))
//                .characterEncoding(StandardCharsets.UTF_8)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-Sharer-User-Id", 1)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(400));
//
//        Mockito.verify(itemRequestService, Mockito.never()).createRequest(any(), anyLong());
//    }

    @Test
    @SneakyThrows
    void getUserRequests_shouldReturnOk() {
        when(itemRequestService.getUserRequests(anyLong())).thenReturn(List.of(dto));

        mvc.perform(get("/requests")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @SneakyThrows
    void getAllRequests() {
        List<ItemRequestDto> requestDtos = List.of(dto);
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(requestDtos);

        mvc.perform(get("/requests/all").characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        Mockito.verify(itemRequestService, Mockito.atLeastOnce()).getAllRequests(1L, 0, 20);
    }

    @Test
    @SneakyThrows
    void getRequestById() {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(dto);

        mvc.perform(get("/requests/1").characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(dto.getDescription())));
    }
}