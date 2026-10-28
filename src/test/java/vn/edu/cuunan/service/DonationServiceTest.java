package vn.edu.cuunan.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.cuunan.config.VnpayConfig;
import vn.edu.cuunan.dto.DonationRequestDto;
import vn.edu.cuunan.entity.Donation;
import vn.edu.cuunan.repository.DonationRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private VnpayConfig vnpayConfig;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private DonationService donationService;

    @Test
    @DisplayName("Tạo đóng góp VNPay tạo bản ghi PENDING và trả về URL VNPay")
    void testCreateDonationVnpay() {
        DonationRequestDto dto = new DonationRequestDto(
                "Nguyễn Văn An",
                "0988888888",
                "an@gmail.com",
                new BigDecimal("500000"),
                "Ủng hộ đồng bào",
                "Quảng Bình",
                "VNPAY"
        );

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(donationRepository.save(any(Donation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(vnpayConfig.buildPaymentUrl(anyString(), eq(500000L), anyString(), eq(request)))
                .thenReturn("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?fake=1");

        Map<String, Object> result = donationService.createDonation(dto, request);

        assertNotNull(result);
        assertEquals("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?fake=1", result.get("redirectUrl"));
        assertNotNull(result.get("transactionCode"));
        verify(donationRepository, times(1)).save(any(Donation.class));
    }

    @Test
    @DisplayName("Tạo đóng góp VietQR chuyển hướng tới trang hiển thị mã QR")
    void testCreateDonationVietQR() {
        DonationRequestDto dto = new DonationRequestDto(
                "Trần Thị B",
                "0977777777",
                "b@gmail.com",
                new BigDecimal("200000"),
                "Thương miền Trung",
                "Huế",
                "VIETQR"
        );

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(donationRepository.save(any(Donation.class))).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> result = donationService.createDonation(dto, request);

        assertNotNull(result);
        String redirectUrl = (String) result.get("redirectUrl");
        assertTrue(redirectUrl.startsWith("/donations/pay/"));
    }

    @Test
    @DisplayName("Xử lý callback VNPay thành công cập nhật SUCCESS và gửi email cảm ơn")
    void testProcessVnpayReturnSuccess() {
        String txCode = "CN12345678";
        Donation donation = Donation.builder()
                .id(1L)
                .transactionCode(txCode)
                .amount(new BigDecimal("300000"))
                .donorEmail("donor@gmail.com")
                .status("PENDING")
                .build();

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("vnp_TxnRef", txCode);
        queryParams.put("vnp_ResponseCode", "00");
        queryParams.put("vnp_SecureHash", "mockedHash");

        when(donationRepository.findByTransactionCode(txCode)).thenReturn(Optional.of(donation));
        when(vnpayConfig.validateSignature(anyMap(), eq("mockedHash"))).thenReturn(true);
        when(donationRepository.save(any(Donation.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Donation> result = donationService.processVnpayReturn(queryParams);

        assertTrue(result.isPresent());
        assertEquals("SUCCESS", result.get().getStatus());
        assertNotNull(result.get().getPaidAt());
        verify(emailService, times(1)).sendDonationThankYouEmail(any(Donation.class));
    }

    @Test
    @DisplayName("Xác nhận chuyển khoản VietQR thủ công cập nhật trạng thái SUCCESS")
    void testConfirmManualPayment() {
        String txCode = "CN99999999";
        Donation donation = Donation.builder()
                .id(2L)
                .transactionCode(txCode)
                .amount(new BigDecimal("1000000"))
                .status("PENDING")
                .build();

        when(donationRepository.findByTransactionCode(txCode)).thenReturn(Optional.of(donation));
        when(donationRepository.save(any(Donation.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Donation> result = donationService.confirmManualPayment(txCode);

        assertTrue(result.isPresent());
        assertEquals("SUCCESS", result.get().getStatus());
        assertNotNull(result.get().getPaidAt());
    }

    @Test
    @DisplayName("Lấy thống kê tổng số tiền và lượt ủng hộ chính xác")
    void testGetDonationStats() {
        when(donationRepository.sumAmountByStatus("SUCCESS")).thenReturn(new BigDecimal("15000000"));
        when(donationRepository.countByStatus("SUCCESS")).thenReturn(45L);

        Map<String, Object> stats = donationService.getDonationStats();

        assertNotNull(stats);
        assertEquals(new BigDecimal("15000000"), stats.get("totalAmount"));
        assertEquals(45L, stats.get("totalCount"));
        assertEquals(new BigDecimal("500000000"), stats.get("targetAmount"));
    }
}
