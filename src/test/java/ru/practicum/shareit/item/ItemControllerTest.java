package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    ItemService itemService;

    @InjectMocks
    ItemController itemController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemDto dto;

    private ItemExtendedDto extendedDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        dto = ItemDto.builder()
                .description("desc")
                .name("name")
                .available(true)
                .build();

        extendedDto = ItemExtendedDto.builder()
                .description("descExt")
                .name("nameExt")
                .available(true)
                .build();
    }

    @Test
    @SneakyThrows
    void createItem() throws JsonProcessingException {
        when(itemService.createItem(any(ItemDto.class), anyLong()))
                .thenReturn(dto);

        mvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(dto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(dto.getDescription())));

    }

    @Test
    @SneakyThrows
    void createItem_whenUserIdEmpty_thenReturn400() {
        mvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(dto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));

    }

    @Test
    @SneakyThrows
    void updateItem() {
        {
            when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong()))
                    .thenReturn(dto);

            mvc.perform(patch("/items/1")
                    .content(objectMapper.writeValueAsString(dto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", 1)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.description", is(dto.getDescription())));

        }
    }

    @Test
    @SneakyThrows
    void updateItem_whenUserIdEmpty_thenReturn400() {
        {
            mvc.perform(patch("/items/1")
                    .content(objectMapper.writeValueAsString(dto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is(400));
        }
    }

    @Test
    @SneakyThrows
    void getItemById() {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(extendedDto);

        mvc.perform(get("/items/1")
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(extendedDto.getDescription())));
    }

    @Test
    @SneakyThrows
    void getItemById_whenUserIdEmpty_thenReturn400() {
        mvc.perform(get("/items/1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    @SneakyThrows
    void getAllUserItems() {
        when(itemService.getUserItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(extendedDto));

        mvc.perform(get("/items")
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @SneakyThrows
    void getAllUserItems_whenUserIdEmpty_thenReturn400() {
        mvc.perform(get("/items")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    @SneakyThrows
    void searchItemsByName() {
        when(itemService.searchItemsByName(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(dto));

        mvc.perform(get("/items/search")
                .header("X-Sharer-User-Id", 1)
                .param("text", "value")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @SneakyThrows
    void searchItemsByName_whenUserIdEmpty_thenReturn400() {
        mvc.perform(get("/items/search")
                .param("text", "value")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    @SneakyThrows
    void addComment() {
        CommentDto commentDto = CommentDto.builder()
                .text("text")
                .authorName("name")
                .build();
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                .content(objectMapper.writeValueAsString(commentDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }
}