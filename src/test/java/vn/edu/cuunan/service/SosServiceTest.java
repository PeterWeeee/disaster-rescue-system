package vn.edu.cuunan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import vn.edu.cuunan.dto.SosRequestDto;
import vn.edu.cuunan.entity.Role;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.SosRequestRepository;
import vn.edu.cuunan.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SosServiceTest {

    @Mock
    private SosRequestRepository sosRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private SosService sosService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        Role citizenRole = Role.builder().id(4).name("ROLE_CITIZEN").build();
        sampleUser = User.builder()
                .id(10L)
                .username("dan_test")
                .fullName("Nguyễn Dân Test")
                .phone("0988112233")
                .role(citizenRole)
                .build();
    }

    @Test
    @DisplayName("Tạo SOS thành công ở chế độ ẩn danh (Anonymous)")
    void testCreateSosRequest_Anonymous_Success() {
        SosRequestDto dto = SosRequestDto.builder()
                .contactName("Trần Văn A")
                .contactPhone("0912345678")
                .latitude(16.0544)
                .longitude(108.2022)
                .addressDetail("Số 10 đường Trần Phú, Đà Nẵng")
                .urgencyLevel("CRITICAL")
                .peopleCount(3)
                .hasElderly(true)
                .hasChildren(false)
                .hasInjured(true)
                .description("Nước dâng cao 1.5m")
                .build();

        when(sosRequestRepository.save(any(SosRequest.class))).thenAnswer(invocation -> {
            SosRequest req = invocation.getArgument(0);
            req.setId(100L);
            return req;
        });

        SosRequest created = sosService.createSosRequest(dto, null);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(100L);
        assertThat(created.getSender()).isNull();
        assertThat(created.getContactName()).isEqualTo("Trần Văn A");
        assertThat(created.getContactPhone()).isEqualTo("0912345678");
        assertThat(created.getStatus()).isEqualTo("PENDING");
        assertThat(created.getUrgencyLevel()).isEqualTo("CRITICAL");
        assertThat(created.getHasElderly()).isTrue();
        assertThat(created.getHasInjured()).isTrue();
        assertThat(created.getHasChildren()).isFalse();

        verify(userRepository, never()).findById(any());
        verify(sosRequestRepository, times(1)).save(any(SosRequest.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/sos-alerts"), any(Object.class));
    }

    @Test
    @DisplayName("Tạo SOS thành công với người dùng đã đăng nhập")
    void testCreateSosRequest_Authenticated_Success() {
        SosRequestDto dto = SosRequestDto.builder()
                .contactName("Nguyễn Dân Test")
                .contactPhone("0988112233")
                .latitude(16.0600)
                .longitude(108.2100)
                .urgencyLevel("HIGH")
                .peopleCount(2)
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(sosRequestRepository.save(any(SosRequest.class))).thenAnswer(invocation -> {
            SosRequest req = invocation.getArgument(0);
            req.setId(101L);
            return req;
        });

        SosRequest created = sosService.createSosRequest(dto, 10L);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(101L);
        assertThat(created.getSender()).isEqualTo(sampleUser);
        assertThat(created.getSender().getUsername()).isEqualTo("dan_test");
        assertThat(created.getStatus()).isEqualTo("PENDING");

        verify(userRepository, times(1)).findById(10L);
        verify(sosRequestRepository, times(1)).save(any(SosRequest.class));
    }

    @Test
    @DisplayName("Ném lỗi khi vĩ độ (Latitude) vượt quá phạm vi [-90, 90]")
    void testCreateSosRequest_InvalidLatitude() {
        SosRequestDto dto = SosRequestDto.builder()
                .contactName("Lỗi Vĩ Độ")
                .contactPhone("0900000000")
                .latitude(95.0) // Không hợp lệ
                .longitude(108.0)
                .build();

        assertThatThrownBy(() -> sosService.createSosRequest(dto, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Vĩ độ không hợp lệ");
    }

    @Test
    @DisplayName("Ném lỗi khi kinh độ (Longitude) vượt quá phạm vi [-180, 180]")
    void testCreateSosRequest_InvalidLongitude() {
        SosRequestDto dto = SosRequestDto.builder()
                .contactName("Lỗi Kinh Độ")
                .contactPhone("0900000000")
                .latitude(16.0)
                .longitude(185.0) // Không hợp lệ
                .build();

        assertThatThrownBy(() -> sosService.createSosRequest(dto, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Kinh độ không hợp lệ");
    }

    @Test
    @DisplayName("Tìm kiếm SOS theo ID thành công")
    void testGetSosById_Success() {
        SosRequest existing = SosRequest.builder()
                .id(1L)
                .contactName("Người cần cứu")
                .contactPhone("0901234567")
                .latitude(16.0)
                .longitude(108.0)
                .status("PENDING")
                .build();

        when(sosRequestRepository.findById(1L)).thenReturn(Optional.of(existing));

        SosRequest found = sosService.getSosById(1L);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getContactName()).isEqualTo("Người cần cứu");
    }

    @Test
    @DisplayName("Ném lỗi khi tìm kiếm SOS ID không tồn tại")
    void testGetSosById_NotFound() {
        when(sosRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sosService.getSosById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Không tìm thấy yêu cầu cứu nạn #999");
    }

    @Test
    @DisplayName("Cập nhật trạng thái sang RESOLVED tự động gán resolvedAt")
    void testUpdateSosStatus_ToResolved() {
        SosRequest existing = SosRequest.builder()
                .id(5L)
                .contactName("Nguyễn Văn E")
                .contactPhone("0911223344")
                .latitude(16.0)
                .longitude(108.0)
                .status("IN_PROGRESS")
                .build();

        when(sosRequestRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(sosRequestRepository.save(any(SosRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SosRequest updated = sosService.updateSosStatus(5L, "RESOLVED");

        assertThat(updated.getStatus()).isEqualTo("RESOLVED");
        assertThat(updated.getResolvedAt()).isNotNull();
        verify(sosRequestRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Tìm kiếm danh sách SOS theo SĐT người liên hệ")
    void testGetSosByPhone() {
        SosRequest req = SosRequest.builder()
                .id(10L)
                .contactName("Lê Văn Test")
                .contactPhone("0987654321")
                .latitude(16.0)
                .longitude(108.0)
                .status("PENDING")
                .build();

        when(sosRequestRepository.findByContactPhoneOrderByCreatedAtDesc("0987654321"))
                .thenReturn(List.of(req));

        List<SosRequest> results = sosService.getSosByPhone("0987654321");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getContactName()).isEqualTo("Lê Văn Test");
    }

    @Test
    @DisplayName("Đếm số lượng ca cứu nạn đang PENDING")
    void testCountPendingRequests() {
        when(sosRequestRepository.countByStatus("PENDING")).thenReturn(5L);

        long count = sosService.countPendingRequests();
        assertThat(count).isEqualTo(5L);
    }
}
