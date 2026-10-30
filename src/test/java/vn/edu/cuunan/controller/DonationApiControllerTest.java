package vn.edu.cuunan.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import vn.edu.cuunan.dto.SepayWebhookDto;
import vn.edu.cuunan.entity.Donation;
import vn.edu.cuunan.service.DonationService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationApiControllerTest {

    @Mock
    private DonationService donationService;

    @InjectMocks
    private DonationApiController donationApiController;

    private final String API_KEY = "BBE3HIZC69VHCFUVNWDZC0K8ISMAGPXMJXJIFOAQ3BP0JRCUEGW8TSRZOYGLEYF2";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(donationApiController, "sepayApiKey", API_KEY);
    }

    @Test
    @DisplayName("Từ chối webhook nếu sai API Key")
    void testSepayWebhookUnauthorized() {
        SepayWebhookDto dto = new SepayWebhookDto();
        dto.setContent("CN123456");

        ResponseEntity<Map<String, Object>> response = donationApiController.handleSepayWebhook(
                "Apikey WRONG_KEY",
                dto
        );

        assertEquals(401, response.getStatusCode().value());
        assertEquals(false, response.getBody().get("success"));
    }

    @Test
    @DisplayName("Chấp nhận webhook hợp lệ và xác nhận quyên góp thành công")
    void testSepayWebhookSuccess() {
        String txCode = "CN555555";
        SepayWebhookDto dto = new SepayWebhookDto();
        dto.setContent("Ung ho cuu tro lu lut " + txCode);
        dto.setTransferType("in");
        dto.setTransferAmount(new BigDecimal("200000"));

        Donation donation = Donation.builder()
                .id(1L)
                .transactionCode(txCode)
                .amount(new BigDecimal("200000"))
                .status("SUCCESS")
                .build();

        when(donationService.confirmSepayPayment(eq(txCode), eq(new BigDecimal("200000"))))
                .thenReturn(Optional.of(donation));

        ResponseEntity<Map<String, Object>> response = donationApiController.handleSepayWebhook(
                "Apikey " + API_KEY,
                dto
        );

        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, response.getBody().get("success"));
        assertEquals(txCode, response.getBody().get("transactionCode"));
        verify(donationService, times(1)).confirmSepayPayment(eq(txCode), eq(new BigDecimal("200000")));
    }
}
