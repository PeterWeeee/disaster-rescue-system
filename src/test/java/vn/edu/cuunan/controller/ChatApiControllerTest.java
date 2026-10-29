package vn.edu.cuunan.controller;



import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;

import vn.edu.cuunan.dto.ChatMessageDto;

import vn.edu.cuunan.dto.SendChatMessageRequest;

import vn.edu.cuunan.service.ChatService;



import java.time.LocalDateTime;

import java.util.List;

import java.util.Map;



import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest

@AutoConfigureMockMvc

@ActiveProfiles("local")

class ChatApiControllerTest {



    @Autowired

    private MockMvc mockMvc;



    @Autowired

    private ObjectMapper objectMapper;



    @MockBean

    private ChatService chatService;



    @Test

    @DisplayName("GET /api/chat/sos/{id}/messages -> Trả về danh sách tin nhắn 200 OK")

    void testGetMessagesSuccess() throws Exception {

        ChatMessageDto dto = new ChatMessageDto();

        dto.setId(1L);

        dto.setSosId(100L);

        dto.setSenderType("CITIZEN");

        dto.setSenderName("Trần Văn A");

        dto.setContent("Nước ngập ngang ngực rồi!");

        dto.setMessageType("QUICK_ALERT");

        dto.setFormattedTime("14:30");



        Mockito.when(chatService.getMessagesBySosId(eq(100L), any(), any()))

                .thenReturn(List.of(dto));



        mockMvc.perform(get("/api/chat/sos/100/messages"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$[0].id").value(1))

                .andExpect(jsonPath("$[0].senderType").value("CITIZEN"))

                .andExpect(jsonPath("$[0].content").value("Nước ngập ngang ngực rồi!"))

                .andExpect(jsonPath("$[0].formattedTime").value("14:30"));

    }



    @Test

    @DisplayName("POST /api/chat/sos/{id}/messages -> Gửi tin nhắn thành công 201 Created")

    void testSendMessageSuccess() throws Exception {

        SendChatMessageRequest request = new SendChatMessageRequest();

        request.setContent("Đội chuẩn bị tiếp cận nhà bạn nhé");

        request.setSenderType("RESCUER");



        ChatMessageDto dto = new ChatMessageDto();

        dto.setId(2L);

        dto.setSosId(100L);

        dto.setSenderType("RESCUER");

        dto.setSenderName("Đội Cứu Hộ Hải Châu");

        dto.setContent("Đội chuẩn bị tiếp cận nhà bạn nhé");

        dto.setMessageType("TEXT");

        dto.setFormattedTime("14:32");



        Mockito.when(chatService.saveAndBroadcastMessage(eq(100L), any(SendChatMessageRequest.class), any()))

                .thenReturn(dto);



        mockMvc.perform(post("/api/chat/sos/100/messages")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.id").value(2))

                .andExpect(jsonPath("$.senderType").value("RESCUER"))

                .andExpect(jsonPath("$.content").value("Đội chuẩn bị tiếp cận nhà bạn nhé"));

    }



    @Test

    @DisplayName("POST /api/chat/sos/{id}/messages với nội dung rỗng -> Trả về 400 Bad Request")

    void testSendMessageEmptyContent() throws Exception {

        SendChatMessageRequest request = new SendChatMessageRequest();

        request.setContent("");



        mockMvc.perform(post("/api/chat/sos/100/messages")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isBadRequest());

    }



    @Test

    @DisplayName("GET /api/chat/sos/{id}/status -> Trả về trạng thái phòng chat 200 OK")

    void testGetChatStatus() throws Exception {

        Map<String, Object> statusMap = Map.of(

                "sosId", 100L,

                "status", "IN_PROGRESS",

                "canChat", true,

                "isClosed", false,

                "teamName", "Đội Cứu Hộ Hải Châu"

        );



        Mockito.when(chatService.getChatStatus(100L)).thenReturn(statusMap);



        mockMvc.perform(get("/api/chat/sos/100/status"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.sosId").value(100))

                .andExpect(jsonPath("$.canChat").value(true))

                .andExpect(jsonPath("$.teamName").value("Đội Cứu Hộ Hải Châu"));

    }



    @Test

    @DisplayName("GET /api/chat/active-session -> Trả về phiên chat đang hoạt động 200 OK")

    void testGetActiveChatSession() throws Exception {

        Map<String, Object> sessionMap = Map.of(

                "hasActiveChat", true,

                "sosId", 16L,

                "partnerName", "Đội Cứu Hộ Hải Châu",

                "partnerPhone", "0987654321",

                "userRole", "CITIZEN",

                "statusText", "Đội cứu hộ đang tiếp cận bạn"

        );



        Mockito.when(chatService.getActiveChatSession(any(), any())).thenReturn(sessionMap);



        mockMvc.perform(get("/api/chat/active-session"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.hasActiveChat").value(true))

                .andExpect(jsonPath("$.sosId").value(16))

                .andExpect(jsonPath("$.partnerName").value("Đội Cứu Hộ Hải Châu"));

    }

}

