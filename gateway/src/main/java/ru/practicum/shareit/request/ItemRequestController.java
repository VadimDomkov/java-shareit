//package ru.practicum.shareit.request;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import ru.practicum.shareit.request.dto.ItemRequestDto;
//
//import javax.validation.Valid;
//import javax.validation.constraints.Min;
//
///**
// * TODO Sprint add-item-requests.
// */
//@RestController
//@RequestMapping(path = "/requests")
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@Slf4j
//@Validated
//public class ItemRequestController {
//    private final ItemRequestClient itemRequestClient;
//
//    @PostMapping
//    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
//                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
//        log.info("Запрос POST к /requests");
//        return itemRequestClient.createRequest(itemRequestDto, userId);
//    }
//
//    @GetMapping
//    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
//        log.info("Запрос GET к /requests");
//        return itemRequestClient.getUserRequests(userId);
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
//                                                 @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
//                                                 @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
//        log.info("Запрос GET к /requests/all");
//        return itemRequestClient.getAllRequests(userId, from, size);
//    }
//
//    @GetMapping("/{requestId}")
//    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
//        log.info(String.format("Запрос GET к /requests/%d", requestId));
//        return itemRequestClient.getRequestById(userId, requestId);
//    }
//}
