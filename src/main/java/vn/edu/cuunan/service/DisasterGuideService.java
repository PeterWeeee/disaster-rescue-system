package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.cuunan.dto.DisasterGuideDto;
import vn.edu.cuunan.dto.DisasterStepDto;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DisasterGuideService {

    private static final Logger log = LoggerFactory.getLogger(DisasterGuideService.class);

    /**
     * Đường dẫn CDN Cloudinary gốc phục vụ hình ảnh cẩm nang cứu hộ thiên tai.
     * Cấu hình theo cloud-name 'odwf5uoi' của nhóm.
     */
    public static final String CLOUDINARY_GUIDE_BASE_URL = "https://res.cloudinary.com/odwf5uoi/image/upload/cuunan247/guide_images/";

    private final Map<String, DisasterGuideDto> guideMap = new LinkedHashMap<>();
    private final CloudinaryService cloudinaryService;

    public DisasterGuideService(@Autowired(required = false) CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
        initGuides();
    }

    public List<DisasterGuideDto> getAllGuides() {
        return new ArrayList<>(guideMap.values());
    }

    public Optional<DisasterGuideDto> getGuideBySlug(String slug) {
        if (slug == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(guideMap.get(slug.toLowerCase().trim()));
    }

    public DisasterGuideDto getPreviousGuide(String currentSlug) {
        List<DisasterGuideDto> list = getAllGuides();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equalsIgnoreCase(currentSlug)) {
                return (i > 0) ? list.get(i - 1) : list.get(list.size() - 1);
            }
        }
        return null;
    }

    public DisasterGuideDto getNextGuide(String currentSlug) {
        List<DisasterGuideDto> list = getAllGuides();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equalsIgnoreCase(currentSlug)) {
                return (i < list.size() - 1) ? list.get(i + 1) : list.get(0);
            }
        }
        return null;
    }

    /**
     * Cập nhật đường dẫn ảnh cho một bước ứng phó cụ thể (khi tải ảnh mới lên Cloudinary qua API).
     */
    public boolean updateStepImage(String guideSlug, int stepNumber, String newImageUrl) {
        if (guideSlug == null || newImageUrl == null || newImageUrl.isBlank()) {
            return false;
        }
        Optional<DisasterGuideDto> guideOpt = getGuideBySlug(guideSlug);
        if (guideOpt.isEmpty()) {
            return false;
        }
        for (DisasterStepDto step : guideOpt.get().getSteps()) {
            if (step.getStepNumber() == stepNumber) {
                step.setImageUrl(newImageUrl);
                log.info("Cập nhật URL ảnh Cloudinary cẩm nang cho [{}_bước{}]: {}", guideSlug, stepNumber, newImageUrl);
                return true;
            }
        }
        return false;
    }

    /**
     * Đồng bộ toàn bộ các ảnh cẩm nang cục bộ lên Cloudinary (nếu Cloudinary đã được cấu hình hợp lệ).
     */
    public Map<String, String> syncImagesToCloudinary() {
        Map<String, String> syncedUrls = new LinkedHashMap<>();
        if (cloudinaryService == null || !cloudinaryService.isCloudinaryConfigured()) {
            log.info("Cloudinary chưa được cấu hình hoặc offline, cẩm nang duy trì đường dẫn ảnh cục bộ.");
            return syncedUrls;
        }

        for (DisasterGuideDto guide : guideMap.values()) {
            for (DisasterStepDto step : guide.getSteps()) {
                String currentUrl = step.getImageUrl();
                if (currentUrl != null && !currentUrl.startsWith("http")) {
                    String relativePath = currentUrl.startsWith("/") ? currentUrl.substring(1) : currentUrl;
                    Path diskPath = Paths.get("src/main/resources/static", relativePath);
                    if (Files.exists(diskPath)) {
                        String publicId = guide.getId() + "_step" + step.getStepNumber();
                        String cloudUrl = cloudinaryService.uploadLocalFile(diskPath.toFile(), "guide_images", publicId);
                        if (cloudUrl != null && !cloudUrl.isBlank()) {
                            step.setImageUrl(cloudUrl);
                            syncedUrls.put(guide.getId() + "_step" + step.getStepNumber(), cloudUrl);
                        }
                    }
                }
            }
        }
        return syncedUrls;
    }

    private DisasterStepDto buildStep(int number, String title, String headline, String desc,
                                      String imageName, String caption,
                                      List<String> actions, List<String> dos, List<String> donts, String tip) {
        String cloudUrl = CLOUDINARY_GUIDE_BASE_URL + imageName;
        String localFallback = "/images/guide/" + imageName;
        return new DisasterStepDto(number, title, headline, desc, cloudUrl, localFallback, caption, actions, dos, donts, tip);
    }

    private void initGuides() {
        // 01. CHUẨN BỊ TRƯỚC THIÊN TAI
        List<DisasterStepDto> prepSteps = new ArrayList<>();
        prepSteps.add(buildStep(
                1,
                "Theo dõi cảnh báo và thông tin khí tượng",
                "Cập nhật thường xuyên từ cơ quan khí tượng thủy văn và chính quyền địa phương",
                "Thông tin kịp thời là yếu tố sống còn trước mọi biến cố thiên tai. Hãy luôn cài đặt thông báo khẩn cấp, theo dõi bản tin dự báo áp thấp/bão và hướng dẫn sơ tán của Ban chỉ huy Phòng chống thiên tai địa phương.",
                "prep-step1.jpg",
                "Theo dõi thông báo thời tiết, bản đồ radar và bật cảnh báo khẩn cấp (Hình ảnh AI minh họa)",
                List.of(
                        "Cài đặt ứng dụng Cứu Nạn 24/7 và bật định vị để nhận cảnh báo theo vùng địa lý.",
                        "Theo dõi các kênh phát thanh quốc gia, đài truyền hình địa phương và cổng thông tin chính phủ.",
                        "Kiểm tra mức độ cảnh báo thiên tai (Cấp 1 đến Cấp 5) để có phương án đối phó tương ứng."
                ),
                List.of("Lưu sẵn danh bạ cứu hộ 112, 114, 115 và số điều phối viên khu vực", "Sạc pin đầy đủ cho điện thoại và pin sạc dự phòng", "Thông báo cho người thân trong gia đình về tình hình"),
                List.of("Không tin vào tin đồn thất thiệt chưa kiểm chứng trên mạng xã hội", "Không chủ quan khi thấy trời bớt mưa hoặc tạm ngớt gió"),
                "Mẹo sống còn: Chuẩn bị 1 chiếc radio chạy pin hoặc sạc tay để nghe tin tức khi mất điện và mất sóng viễn thông."
        ));

        prepSteps.add(buildStep(
                2,
                "Chuẩn bị túi cứu trợ khẩn cấp (Go-Bag)",
                "Bộ trang bị sinh tồn tối thiểu đủ duy trì cho gia đình trong 72 giờ",
                "Khi có lệnh sơ tán khẩn cấp, bạn chỉ có vài phút để rời khỏi nơi nguy hiểm. Một chiếc ba lô khẩn cấp chuẩn bị sẵn sẽ bảo đảm an toàn tính mạng và sức khỏe cho cả gia đình.",
                "prep-step2.jpg",
                "Túi đồ sinh tồn khẩn cấp gồm nước uống, lương khô, thuốc men, đèn pin và sạc dự phòng (Hình ảnh AI minh họa)",
                List.of(
                        "Nước sạch: Ít nhất 2-3 lít nước uống đóng chai cho mỗi người/ngày.",
                        "Lương thực khô: Lương khô quân đội, đồ hộp có nắp mở sẵn, bánh quy năng lượng cao.",
                        "Y tế: Bông băng, gạc vô trùng, thuốc sát trùng, thuốc hạ sốt, thuốc đặc trị mãn tính của người thân.",
                        "Dụng cụ: Đèn pin chống nước, còi cứu hộ, dao đa năng, bật lửa chống gió, pin dự phòng sạc đầy.",
                        "Giấy tờ: CCCD, sổ hộ khẩu, giấy tờ nhà đất bọc trong túi chống nước zip kín."
                ),
                List.of("Để ba lô ở vị trí dễ lấy gần cửa ra vào", "Kiểm tra hạn sử dụng thực phẩm và thuốc định kỳ 6 tháng/lần", "Chuẩn bị thêm tã sữa nếu gia đình có trẻ sơ sinh"),
                List.of("Không mang vác đồ đạc cồng kềnh, nặng nề làm chậm tốc độ sơ tán", "Không cất giấy tờ quan trọng ở ngăn dễ ngấm nước"),
                "Mẹo sống còn: Mỗi thành viên nên có một tấm thẻ đeo cổ ghi rõ họ tên, nhóm máu, số liên hệ khẩn cấp và tiền sử bệnh lý."
        ));

        prepSteps.add(buildStep(
                3,
                "Gia cố, chằng chống nhà cửa và tài sản",
                "Bảo vệ kết cấu mái, cửa sổ và ngăn chặn nước tràn vào tầng trệt",
                "Gió bão có thể giật bay mái tôn và làm vỡ các ô kính lớn gây chấn thương nghiêm trọng. Việc chủ động chằng chống và xếp bao cát sẽ giảm thiểu tối đa thiệt hại vật chất.",
                "prep-step3.jpg",
                "Chằng chống mái tôn, dán băng keo cửa kính chữ X và xếp bao cát ngăn nước tràn (Hình ảnh AI minh họa)",
                List.of(
                        "Dùng bao cát hoặc thanh gỗ, dây cáp thép chằng néo cố định mái tôn.",
                        "Dán băng keo bản to hình chữ X hoặc dấu hoa thị lên cửa kính để hạn chế mảnh vỡ văng xa nếu kính vỡ.",
                        "Đắp đê bao cát cao 40-60cm trước cửa chính và cửa thoát nước để cản lũ lụt ban đầu.",
                        "Cắt tỉa cành cây lớn, chằng giữ biển hiệu hoặc chậu cây cảnh ngoài ban công."
                ),
                List.of("Khóa chặt cửa sổ, cửa chớp hướng đón gió bão", "Kê cao bàn ghế, tivi, tủ lạnh lên tầng hoặc bệ đỡ", "Lau dọn rãnh thoát nước mái và sân thượng"),
                List.of("Không leo lên mái nhà chằng chống khi trời đã bắt đầu mưa to gió lớn", "Không đứng gần cửa kính đón gió trực diện"),
                "Mẹo sống còn: Tắt atomat (cầu dao) các mạch điện ngoài trời và tầng trệt trước khi nước ngập chạm ổ cắm."
        ));

        prepSteps.add(buildStep(
                4,
                "Lập kế hoạch sơ tán và thống nhất kênh liên lạc",
                "Xác định lộ trình di chuyển an toàn và điểm hẹn gia đình",
                "Trong tình huống khẩn cấp, viễn thông có thể gián đoạn cục bộ. Việc cả gia đình cùng nắm rõ lộ trình sơ tán và địa điểm tập kết an toàn sẽ tránh việc lạc mất nhau.",
                "prep-step4.jpg",
                "Gia đình cùng xem bản đồ tuyến đường sơ tán lên vùng cao và danh sách điểm trú ẩn (Hình ảnh AI minh họa)",
                List.of(
                        "Xác định địa điểm trú ẩn an toàn gần nhất: Trường học kiên cố, trụ sở UBND, trạm y tế vùng cao.",
                        "Vẽ sơ đồ thoát hiểm với ít nhất 2 lộ trình (đề phòng 1 đường bị ngập sâu hoặc sạt lở).",
                        "Chỉ định một người thân ở ngoài vùng thiên tai làm đầu mối tiếp nhận thông tin liên lạc cho cả nhà.",
                        "Ghi nhớ quy ước gửi tin nhắn SMS thay vì gọi thoại khi mạng viễn thông nghẽn mạch."
                ),
                List.of("Tập dượt di chuyển cho người già và trẻ nhỏ", "Quy định rõ điểm gặp mặt nếu bị tách đoàn", "Ưu tiên giúp đỡ hàng xóm neo đơn hoặc tàn tật"),
                List.of("Không đi vào các tuyến đường đất yếu ven sông suối", "Không chờ nước ngập quá gối mới bắt đầu di tản"),
                "Mẹo sống còn: Tin nhắn SMS tiêu tốn rất ít băng thông và sẽ tự động gửi đi ngay khi có sóng chập chờn."
        ));

        DisasterGuideDto prepGuide = new DisasterGuideDto(
                "preparation",
                "01",
                "TRƯỚC KHI THIÊN TAI",
                "Chuẩn bị sẵn sàng cho gia đình",
                "Túi đồ và kế hoạch gia đình",
                "Sự chuẩn bị kỹ lưỡng từ trước là chìa khóa bảo vệ an toàn cao nhất cho bạn và người thân trước mọi cơn bão, lũ lụt hay biến cố thiên tai bất ngờ.",
                "Theo dõi cảnh báo chính thức, chủ động tích trữ nhu yếu phẩm và sơ tán ngay khi nhận lệnh của chính quyền.",
                "fa-solid fa-clipboard-list",
                "topic-blue",
                List.of(
                        "Theo dõi thông báo từ cơ quan chức năng và chính quyền địa phương.",
                        "Chuẩn bị túi khẩn cấp: nước uống, thực phẩm, đèn pin, pin dự phòng, thuốc cần thiết, giấy tờ quan trọng và vật dụng sơ cứu.",
                        "Lưu số liên lạc khẩn cấp, địa điểm sơ tán và thống nhất cách liên lạc với người thân.",
                        "Bọc kín giấy tờ, bảo vệ thiết bị điện và tài sản thiết yếu khi có thể làm an toàn."
                ),
                prepSteps,
                List.of(
                        "Nước uống và thực phẩm khô đủ dùng 72 giờ",
                        "Đèn pin, pin dự phòng sạc đầy và đài radio",
                        "Thuốc thiết yếu và hộp sơ cứu cá nhân",
                        "Giấy tờ quan trọng bọc trong túi zip chống nước",
                        "Số điện thoại khẩn cấp 112, 114, 115 đã lưu",
                        "Lộ trình và địa điểm sơ tán đã thống nhất với người thân"
                ),
                "Nếu gia đình có người cao tuổi, phụ nữ có thai hoặc trẻ sơ sinh, hãy chủ động liên hệ đội cứu nạn để được ưu tiên di dời sớm."
        );
        guideMap.put("preparation", prepGuide);

        // 02. BÃO VÀ GIÓ MẠNH
        List<DisasterStepDto> stormSteps = new ArrayList<>();
        stormSteps.add(buildStep(
                1,
                "Trú ẩn trong phòng kiên cố, tránh xa cửa kính",
                "Chọn phòng ở trung tâm ngôi nhà, không có cửa sổ hoặc có tường gạch dày",
                "Gió bão cấp 10-12 trở lên có sức tàn phá khủng khiếp. Mảnh kính vỡ do áp suất gió hoặc vật thể bay có thể gây sát thương nghiêm trọng.",
                "storm-step1.jpg",
                "Gia đình trú ẩn an toàn trong gian phòng kiên cố không có cửa sổ khi tâm bão quét qua (Hình ảnh AI minh họa)",
                List.of(
                        "Đóng chặt tất cả các cửa ra vào và cửa sổ, khóa chốt an toàn.",
                        "Tránh xa cửa sổ bằng kính, giếng trời và mái lấy sáng.",
                        "Nếu nhà cấp 4 lợp ngói/tôn yếu, hãy sang trú ẩn nhờ nhà kiên cố hoặc trụ sở công cộng lân cận trước khi gió mạnh."
                ),
                List.of("Ngồi sát sàn nhà hoặc nấp dưới gầm phản/giường chắc chắn nếu nghe tiếng mái nhà rung lắc", "Giữ bình tĩnh và động viên tinh thần trẻ nhỏ"),
                List.of("Tuyệt đối không ra ngoài trời xem bão hoặc chụp ảnh quay phim", "Không đứng gần cửa sổ để nhìn ra ngoài"),
                "Mẹo sống còn: Mang sẵn nón bảo hiểm xe máy trong nhà; đội nón bảo hiểm nếu mái nhà có dấu hiệu bị giật bung."
        ));

        stormSteps.add(buildStep(
                2,
                "Ngắt cầu dao điện tổng và khóa van bình gas",
                "Phòng ngừa nguy cơ chập cháy điện và cháy nổ do gió bão gây ra",
                "Khi giông bão, mái nhà dột nước hoặc cây đổ làm đứt đường dây điện có thể truyền điện thế cao vào nhà gây hỏa hoạn và giật điện tử vong.",
                "storm-step2.jpg",
                "Thao tác ngắt cầu dao tổng (aptomat) và khóa chặt van gas an toàn trước bão (Hình ảnh AI minh họa)",
                List.of(
                        "Đứng trên tấm ván khô hoặc dép cao su khi gạt cầu dao tổng.",
                        "Rút toàn bộ phích cắm tủ lạnh, tivi, máy tính ra khỏi ổ điện.",
                        "Vặn chặt van bình gas theo chiều kim đồng hồ."
                ),
                List.of("Dùng đèn pin LED thay cho nến để thắp sáng, tránh nguy cơ bén lửa", "Kiểm tra xem dây điện xung quanh nhà có bị cành cây tì đè"),
                List.of("Không chạm tay ướt vào bảng điện", "Không bật bếp gas khi chưa kiểm tra rò rỉ khí"),
                "Mẹo sống còn: Dùng đèn pin điện thoại hoặc đèn sạc; không bao giờ để nến cháy qua đêm khi cả nhà đi ngủ."
        ));

        stormSteps.add(buildStep(
                3,
                "Tuyệt đối không ra ngoài khi bão đang đổ bộ",
                "Cảnh giác cao độ với bẫy mắt bão (tâm bão lặng gió tạm thời)",
                "Nhiều trường hợp thương vong xảy ra khi người dân tưởng bão đã tan lúc tâm bão đi qua và vội vã ra đường dọn dẹp, sau đó gió giật ngược chiều với cường độ dữ dội hơn.",
                "storm-step3.svg",
                "Cảnh báo nguy hiểm từ tôn bay, cây đổ và bẫy tâm bão lặng gió (Đồ họa an toàn trực quan)",
                List.of(
                        "Chỉ ra ngoài khi có thông báo bão đã suy yếu và tan hoàn toàn từ cơ quan khí tượng.",
                        "Không đứng dưới gốc cây to, cột điện, trạm biến áp hoặc bảng quảng cáo ngoài trời.",
                        "Tránh xa các bức tường gạch bao quanh hoặc bờ kè xây tạm bợ."
                ),
                List.of("Ở yên trong nơi trú ẩn ít nhất 2 giờ sau khi gió tạm lắng", "Lắng nghe tin tức qua đài phát thanh"),
                List.of("Không ra đường thu dọn tôn hay đồ đạc bị gió cuốn", "Không chạm vào bất kỳ sợi dây kim loại nào rơi dưới đất"),
                "Mẹo sống còn: Mọi dây điện rơi trên mặt đường ngập nước phải được coi là đang có điện tử thần!"
        ));

        DisasterGuideDto stormGuide = new DisasterGuideDto(
                "storm",
                "02",
                "BÃO VÀ GIÓ MẠNH",
                "Ở trong nơi trú ẩn kiên cố",
                "Trú ẩn và sơ tán an toàn",
                "Khi bão đổ bộ với sức gió giật mạnh, sự kiên cố của nơi trú ẩn và việc tuân thủ triệt để nguyên tắc không ra đường sẽ bảo đảm an toàn tính mạng cho bạn.",
                "Gia cố nhà cửa, khóa kín cửa, ngắt nguồn điện và kiên nhẫn ở trong nơi an toàn cho tới khi bão tan hẳn.",
                "fa-solid fa-wind",
                "topic-red",
                List.of(
                        "Theo dõi cảnh báo chính thức; gia cố nhà cửa khi điều kiện còn an toàn.",
                        "Đóng cửa, tránh xa cửa kính và các vật có nguy cơ rơi hoặc vỡ.",
                        "Không ra ngoài khi gió mạnh; tránh cây lớn, biển quảng cáo, cột điện và công trình không an toàn.",
                        "Sơ tán theo hướng dẫn của cơ quan chức năng khi được yêu cầu."
                ),
                stormSteps,
                List.of(
                        "Cửa ra vào và cửa sổ được chốt chặn kỹ lưỡng",
                        "Kính cửa sổ được dán băng dính chịu lực",
                        "Đã ngắt cầu dao điện tổng và khóa van gas",
                        "Đã chuyển sang phòng kín kiên cố nhất trong nhà",
                        "Sạc dự phòng và đèn pin để ngay cạnh tay"
                ),
                "Nếu mái nhà bị cuốn phăng, hãy lập tức ôm đầu chui xuống gầm giường kiên cố hoặc ôm lấy người thân trong góc tường chịu lực."
        );
        guideMap.put("storm", stormGuide);

        // 03. LŨ LỤT VÀ NGẬP SÂU
        List<DisasterStepDto> floodSteps = new ArrayList<>();
        floodSteps.add(buildStep(
                1,
                "Di chuyển người và tài sản lên vị trí cao",
                "Ưu tiên tính mạng con người, sơ tán lên tầng cao hoặc gác lửng",
                "Nước lũ có thể dâng cao tính bằng centimet mỗi phút trong đêm tối. Việc di chuyển khẩn trương lên tầng cao cùng túi đồ cứu nạn là bước đầu tiên cần làm.",
                "flood-step1.jpg",
                "Gia đình mang theo túi cứu trợ và giấy tờ di chuyển lên tầng cao khi nước lũ dâng (Hình ảnh AI minh họa)",
                List.of(
                        "Đưa trẻ em, người già và người bệnh lên tầng 2 hoặc khu vực cao ráo trước tiên.",
                        "Mang theo túi đồ khẩn cấp Go-Bag, đèn pin, áo phao và nước uống.",
                        "Di chuyển tài sản quý giá, tài liệu y tế lên bàn cao hoặc giá treo trên cao."
                ),
                List.of("Chuẩn bị sẵn búa, rìu hoặc vật nhọn nếu trú ẩn trên gác xép để phá mái ngói thoát hiểm khi nước ngập nóc", "Giữ ấm cơ thể bằng áo ấm hoặc chăn giữ nhiệt"),
                List.of("Không cố giữ xe cộ hay đồ đạc nặng khi nước đang dâng nhanh", "Không nhốt thú nuôi trong chuồng kín"),
                "Mẹo sống còn: Nếu phải trèo lên gác xép sát mái ngói, luôn mang theo công cụ để phá ngói trèo lên nóc nhà khi nước dâng đỉnh."
        ));

        floodSteps.add(buildStep(
                2,
                "Mặc áo phao và phát tín hiệu cứu hộ",
                "Sử dụng còi, đèn pin và thiết bị phát sáng để đội cứu nạn định vị",
                "Giữa biển nước mênh mông hoặc đêm tối mịt mù, việc phát tín hiệu âm thanh và ánh sáng chuẩn xác giúp đội xuồng máy cứu hộ tiếp cận nhanh nhất.",
                "flood-step2.jpg",
                "Người dân mặc áo phao phản quang, dùng còi và đèn pin phát tín hiệu cho thuyền cứu trợ (Hình ảnh AI minh họa)",
                List.of(
                        "Mặc áo phao cứu sinh đúng quy cách, thắt chặt các quai đai an toàn quanh ngực và bẹn.",
                        "Nếu không có áo phao, tận dụng can nhựa 5L rỗng đậy chặt nắp, săm xe máy hoặc bè chuối làm phao tạm.",
                        "Dùng còi thổi 3 hồi ngắn liên tục hoặc bật đèn nhấp nháy về hướng xuồng cứu hộ.",
                        "Thông báo vị trí chính xác cho người thân và đội cứu nạn qua điện thoại."
                ),
                List.of("Vẫy cờ hoặc vải màu sáng nổi bật (cam, đỏ, vàng) vào ban ngày", "Giữ pin điện thoại để báo vị trí cho đội cứu hộ"),
                List.of("Không nhảy xuống dòng nước sâu để bơi đón thuyền cứu nạn", "Không để trẻ em tự do di chuyển ngoài ban công trơn trượt"),
                "Mẹo sống còn: 3 tiếng còi ngắn – 3 tiếng còi dài – 3 tiếng còi ngắn là mã Morse SOS quốc tế mọi lực lượng cứu hộ đều nhận ra."
        ));

        DisasterGuideDto floodGuide = new DisasterGuideDto(
                "flood",
                "03",
                "LŨ LỤT VÀ NGẬP SÂU",
                "Tránh nước chảy và nguy cơ điện giật",
                "Nước chảy xiết và điện",
                "Nước ngập sâu tiềm ẩn nguy cơ chết đuối, cuốn trôi và điện giật ngầm. Hãy luôn giữ vị trí cao ráo, mang phao cứu sinh và liên tục phát tín hiệu trợ giúp.",
                "Di chuyển lên nơi cao, không vượt dòng nước chảy xiết và chỉ sử dụng nguồn nước đã được đun sôi khử trùng.",
                "fa-solid fa-house-flood-water",
                "topic-teal",
                List.of(
                        "Di chuyển đến nơi cao, an toàn theo tuyến sơ tán được hướng dẫn.",
                        "Không đi bộ, đi xe máy hoặc lái ô tô qua dòng nước chảy xiết hay đoạn ngập không rõ độ sâu.",
                        "Chỉ ngắt điện nếu có thể làm từ nơi khô ráo, an toàn; không chạm thiết bị điện khi đang đứng trong nước.",
                        "Không uống nước bị ô nhiễm; không quay lại vùng nguy hiểm cho đến khi có thông báo an toàn."
                ),
                floodSteps,
                List.of(
                        "Áo phao hoặc can phao tự chế sẵn sàng cho từng người",
                        "Nguồn điện tầng trệt đã được cắt từ vị trí khô",
                        "Nước sạch đóng chai và đồ hộp để trên cao",
                        "Còi cứu hộ và đèn pin chiếu xa hoạt động tốt",
                        "Thông tin gia đình đã gửi đến ban chỉ đạo cứu nạn địa phương"
                ),
                "Một dòng nước chảy xiết chỉ sâu 15cm đã có thể làm ngã người trưởng thành; sâu 30cm có thể cuốn trôi xe ô tô cỡ nhỏ."
        );
        guideMap.put("flood", floodGuide);

        // 04. LŨ QUÉT VÀ SẠT LỞ ĐẤT
        List<DisasterStepDto> landslideSteps = new ArrayList<>();
        landslideSteps.add(buildStep(
                1,
                "Nhận diện sớm các dấu hiệu sạt trượt đất đá",
                "Quan sát mặt đất, sườn đồi, nguồn nước suối và âm thanh từ lòng đất",
                "Lũ quét và sạt lở đồi núi xảy ra cực kỳ chớp nhoáng với sức hủy diệt khủng khiếp. Phát hiện sớm các tín hiệu cảnh báo tự nhiên sẽ giúp bạn có đủ thời gian vàng để thoát thân.",
                "landslide-step1.jpg",
                "Dấu hiệu nứt đất trên sườn đồi, cây cối nghiêng ngả và nước suối chuyển màu đục ngầu (Hình ảnh AI minh họa)",
                List.of(
                        "Nước suối hoặc sông đột ngột chuyển từ trong sang màu nâu đục, mực nước tăng vọt bất thường.",
                        "Xuất hiện các vết nứt mới trên mặt đất, sườn dốc hoặc tường nhà nứt toác.",
                        "Cây cối, hàng rào hoặc cột điện trên sườn đồi bị nghiêng ngả.",
                        "Nghe thấy tiếng gầm rú ầm ầm từ phía thượng nguồn núi như tiếng động cơ máy bay rền vang."
                ),
                List.of("Phát loa báo động khẩn cấp cho cả thôn bản ngay khi phát hiện", "Sẵn sàng chạy thoát hiểm ngay lập tức"),
                List.of("Không nán lại để gom đồ đạc tài sản quý giá", "Không coi thường vết nứt đất nhỏ sau nhiều ngày mưa dầm"),
                "Mẹo sống còn: Sau mưa dầm nhiều ngày, đất trên núi đã no nước và có thể sụt trượt bất cứ lúc nào, kể cả khi trời vừa tạnh mưa!"
        ));

        landslideSteps.add(buildStep(
                2,
                "Thoát hiểm vuông góc với hướng dòng chảy sạt trượt",
                "Chạy thật nhanh sang hai bên sườn thay vì chạy xuôi theo chiều dốc",
                "Dòng lũ quét và bùn đất lao xuống núi với vận tốc lên tới 50-80 km/h, không ai có thể chạy đua tốc độ xuôi dốc. Hãy chạy ngang sang hai bên sườn đồi để thoát khỏi tâm dòng chảy.",
                "landslide-step2.jpg",
                "Người dân chạy thoát hiểm theo hướng vuông góc sang hai bên sườn dốc lên nền đất cứng (Hình ảnh AI minh họa)",
                List.of(
                        "Xác định hướng dòng đất đá đang đổ xuống và chạy theo phương vuông góc (90 độ) sang hai bên.",
                        "Hướng về các mỏm đá hoặc gò đất cao có nền địa chất ổn định, không có vết nứt.",
                        "Hỗ trợ người chạy chậm nhưng tuyệt đối không quay đầu ngược lại lòng khe suối."
                ),
                List.of("Bỏ lại toàn bộ vật nặng cản trở việc chạy bộ", "Hô to để người khác biết hướng thoát nạn"),
                List.of("Không chạy theo lòng khe rãnh thoát nước", "Không nấp dưới gốc cây to mọc trên sườn đất mềm"),
                "Mẹo sống còn: Đất đá sẽ cuốn theo quán tính lòng chảo; chỉ cần chạy lệch sang sườn đồi 30-50m là bạn đã thoát khỏi dòng lũ chính."
        ));

        landslideSteps.add(buildStep(
                3,
                "Tránh xa khe suối, lòng vực và chân taluy dốc đứng",
                "Không dựng lều trại, trú ẩn ở các vị trí tụ thủy nguy hiểm",
                "Chân taluy đường giao thông hoặc các đoạn cong của suối núi là nơi bùn đất va đập mạnh nhất.",
                "landslide-step3.svg",
                "Sơ đồ cảnh báo vùng nguy cơ cao: lòng suối và chân vách taluy dựng đứng (Đồ họa an toàn trực quan)",
                List.of(
                        "Không đi qua các cây cầu tạm hoặc đường tràn ngập nước xiết vùng miền núi.",
                        "Tránh xa các chân taluy dương mới đào cắt xẻ trong quá trình làm đường.",
                        "Tìm nơi tập kết tại các sân trường, nhà văn hóa xây trên nền đồi bằng phẳng."
                ),
                List.of("Kiểm tra địa hình xung quanh điểm tập kết", "Báo cáo cho lực lượng cứu hộ địa phương"),
                List.of("Không quay lại ngôi nhà đang nằm dưới chân đồi nứt", "Không cố vượt qua đoạn đường vừa sạt trượt lở đất"),
                "Mẹo sống còn: Đất sạt lở thường diễn ra theo nhiều đợt kế tiếp; đợt sạt sau thường to hơn đợt sạt trước!"
        ));

        DisasterGuideDto landslideGuide = new DisasterGuideDto(
                "landslide",
                "04",
                "LŨ QUÉT VÀ SẠT LỞ ĐẤT",
                "Rời khu vực có dấu hiệu bất thường",
                "Dấu hiệu và đường thoát",
                "Lũ quét và sạt trượt đất đá diễn ra bất ngờ và có sức tàn phá khủng khiếp. Nắm chắc dấu hiệu và chạy thoát vuông góc với dòng chảy là nguyên tắc sống còn.",
                "Rời khỏi vùng sạt trượt ngay khi phát hiện nứt đất, suối đục hoặc tiếng gầm; tập kết trên nền đất cao cứng cáp.",
                "fa-solid fa-mountain-sun",
                "topic-amber",
                List.of(
                        "Chú ý đất đá dịch chuyển, vết nứt mới, nước suối đổi màu hoặc mực nước thay đổi đột ngột.",
                        "Khi có cảnh báo, rời khu vực theo tuyến sơ tán an toàn; tránh ven suối, chân taluy và sườn dốc có nguy cơ.",
                        "Không cố vượt dòng lũ hoặc khu vực đất đá đang sạt trượt.",
                        "Báo cho cơ quan chức năng khi phát hiện dấu hiệu nguy hiểm, nếu có thể thực hiện an toàn."
                ),
                landslideSteps,
                List.of(
                        "Biết rõ các khe tụ thủy và sườn đồi dốc quanh nhà",
                        "Đã xác định điểm tập kết trên gò đất cứng cao",
                        "Đèn pin và còi báo động luôn mang theo người",
                        "Đã lưu số hotline cứu nạn miền núi 112"
                ),
                "Nếu không may bị kẹt trong dòng bùn trượt, hãy co cụm người như quả bóng, hai tay ôm chặt đầu để bảo vệ hộp sọ và cố gắng ngoi đầu thở."
        );
        guideMap.put("landslide", landslideGuide);

        // 05. DÔNG VÀ SÉT
        List<DisasterStepDto> lightningSteps = new ArrayList<>();
        lightningSteps.add(buildStep(
                1,
                "Tư thế an toàn khi kẹt ở đồng trống (Lightning Crouch)",
                "Thu nhỏ diện tích tiếp xúc với mặt đất và hạ thấp độ cao cơ thể",
                "Nếu đang ở ngoài đồng trống, trên đồi không có nhà cửa và cảm thấy dựng lông tơ (tích điện), sét sắp đánh xuống. Hãy lập tức thực hiện tư thế an toàn.",
                "lightning-step1.jpg",
                "Tư thế ngồi xổm kiễng gót chân chụm sát, cúi gập đầu, bịt tai giảm thiểu tác hại sét (Hình ảnh AI minh họa)",
                List.of(
                        "Ngồi xổm trên hai đầu ngón chân, hai gót chân chụm sát vào nhau.",
                        "Cúi đầu gập sát đầu gối, hai tay ôm chặt lấy hai tai để bảo vệ màng nhĩ trước tiếng sét nổ lớn.",
                        "Không nằm úp hoặc nằm ngửa thẳng người trên mặt đất (sẽ tăng diện tích dòng điện chạy qua tim)."
                ),
                List.of("Vứt bỏ ngay các vật thể kim loại như cuốc xẻng, cần câu, ô dù có nan kim loại ra xa ít nhất 5 mét", "Tách rời nhau ít nhất 3-5 mét nếu đi theo nhóm"),
                List.of("Không nằm rạp người xuống đất", "Không đứng chụm thành nhóm đông người"),
                "Mẹo sống còn: Hai gót chân chụm vào nhau để dòng điện đất nếu có truyền qua chân này sẽ vòng sang chân kia ra ngoài đất, không đi qua tim!"
        ));

        lightningSteps.add(buildStep(
                2,
                "Chọn nơi trú ẩn kiên cố, tránh xa cây cao và kim loại",
                "Vào trong nhà xây kiên cố hoặc xe ô tô vỏ kim loại đóng kín cửa",
                "Cây cao đơn độc và hàng rào sắt là những điểm hút sét tự nhiên nguy hiểm nhất trong cơn dông.",
                "lightning-step2.svg",
                "Phân biệt nơi an toàn (nhà kiên cố, ô tô kín) và nơi tử thần (gốc cây to, cột sắt) (Đồ họa an toàn trực quan)",
                List.of(
                        "Vào ngay các tòa nhà kiên cố có lắp cột thu lôi chống sét.",
                        "Nếu đang đi ô tô, hãy ngồi yên trong xe, đóng kín toàn bộ cửa kính (hiệu ứng lồng Faraday sẽ bảo vệ người trong xe).",
                        "Nếu ở trong nhà, tránh xa vòi nước tắm, bồn rửa bát và không dùng điện thoại cắm sạc trực tiếp."
                ),
                List.of("Rút dây ăng-ten tivi và phích cắm mạng internet", "Đứng cách xa cửa sổ kính ít nhất 1.5m"),
                List.of("Tuyệt đối không trú mưa dưới gốc cây to, chòi nghỉ đơn độc giữa đồng", "Không đứng gần cột đèn chiếu sáng, hàng rào thép gai"),
                "Mẹo sống còn: Tuân thủ quy tắc 30/30: Thấy chớp đếm dưới 30 giây đã nghe sấm thì phải trú ngay; ở yên trong nhà thêm 30 phút sau tiếng sấm cuối cùng."
        ));

        DisasterGuideDto lightningGuide = new DisasterGuideDto(
                "lightning",
                "05",
                "DÔNG VÀ SÉT",
                "Tìm nơi trú kín, chắc chắn",
                "Chọn nơi trú phù hợp",
                "Sét có điện thế hàng triệu vôn và nhiệt độ cực lớn. Biết cách chọn vị trí trú ẩn và tư thế ngồi chuẩn sẽ loại bỏ 95% nguy cơ bị sét đánh trúng.",
                "Vào nhà kiên cố hoặc xe kín; nếu ở đồng trống, thực hiện tư thế ngồi xổm kiễng gót chân, tuyệt đối không trú dưới gốc cây cô độc.",
                "fa-solid fa-cloud-bolt",
                "topic-purple",
                List.of(
                        "Vào trong nhà kiên cố hoặc phương tiện có mái che phù hợp.",
                        "Tránh cây đơn độc, cột điện, nơi trống trải và khu vực có nước.",
                        "Hạn chế sử dụng thiết bị điện có dây trong lúc dông sét.",
                        "Không trú mưa dưới cây lớn hoặc công trình không bảo đảm an toàn."
                ),
                lightningSteps,
                List.of(
                        "Thuộc lòng quy tắc 30/30 về khoảng cách sấm sét",
                        "Biết tư thế ngồi xổm kiễng gót khi ở bãi trống",
                        "Đã rút các thiết bị điện tử nhạy cảm trong nhà",
                        "Không chạm vào nước máy hoặc đồ kim loại trong cơn dông"
                ),
                "Người bị sét đánh không giữ điện tích trong cơ thể; hãy lập tức tiến hành hô hấp nhân tạo ép tim ngoài lồng ngực (CPR) và gọi cấp cứu 115 ngay."
        );
        guideMap.put("lightning", lightningGuide);

        // 06. ĐỘNG ĐẤT
        List<DisasterStepDto> eqSteps = new ArrayList<>();
        eqSteps.add(buildStep(
                1,
                "Quy tắc vàng: Cúi xuống - Che chắn - Bám chặt (Drop - Cover - Hold on)",
                "Hành động ngay lập tức trong 5 giây đầu tiên khi cảm nhận thấy rung chấn",
                "Đa số chấn thương trong động đất gây ra do mảnh vỡ kính, tủ kệ và trần nhà đổ sập xuống. Thao tác chuẩn Drop - Cover - Hold on sẽ bảo vệ các cơ quan nội tạng và hộp sọ.",
                "earthquake-step1.svg",
                "Quy trình Drop (Hạ thấp) – Cover (Che chắn dưới bàn) – Hold On (Bám chặt chân bàn) (Đồ họa chuẩn quốc tế)",
                List.of(
                        "CÚI XUỐNG: Ngay khi sàn nhà rung lắc, hạ thấp trọng tâm bò trên hai đầu gối và hai tay để không bị hất ngã.",
                        "CHE CHẮN: Nhanh chóng chui xuống gầm bàn làm việc hoặc bàn ăn gỗ dày dặn; một tay ôm chặt gáy che đầu, một tay bám chặt chân bàn.",
                        "BÁM CHẶT: Nắm chặt chân bàn và di chuyển cùng bàn nếu chiếc bàn bị rung dịch chuyển."
                ),
                List.of("Nếu không có bàn, ngồi sát góc tường chịu lực, dùng gối, cặp sách hoặc hai cánh tay che kín đầu gáy", "Tránh xa tủ sách, gương soi, đèn chùm và cửa sổ"),
                List.of("Không chạy hoảng loạn ra cầu thang trong lúc rung chấn đang diễn ra", "Không đứng dưới khung bao cửa gỗ"),
                "Mẹo sống còn: Giữ nguyên vị trí che chắn cho tới khi toàn bộ đồ đạc ngừng rung lắc hoàn toàn."
        ));

        eqSteps.add(buildStep(
                2,
                "Sơ tán an toàn ra quảng trường và khu đất trống",
                "Dùng cầu thang bộ sau khi dứt rung chấn, đề phòng dư chấn mạnh",
                "Sau đợt rung lắc chính thường có các đợt dư chấn kế tiếp làm sập các bức tường đã bị nứt rạn. Hãy sơ tán trật tự ra nơi thoáng đãng ngoài trời.",
                "earthquake-step2.jpg",
                "Người dân sơ tán trật tự bằng cầu thang bộ tập kết an toàn tại quảng trường thoáng đãng (Hình ảnh AI minh họa)",
                List.of(
                        "Khóa van gas, ngắt cầu dao điện trước khi bước ra khỏi nhà nếu an toàn.",
                        "Đi giày dép đế dày để tránh bị giẫm phải mảnh kính vỡ vụn trên sàn.",
                        "Sử dụng cầu thang bộ, tuyệt đối KHÔNG sử dụng thang máy.",
                        "Tập kết tại công viên, sân vận động hoặc bãi đỗ xe không có tòa nhà cao tầng kề bên."
                ),
                List.of("Dùng túi hoặc vật mềm che đầu khi đi dọc lối thoát hiểm", "Cảnh giác với biển quảng cáo hoặc gạch ốp tường có thể rơi"),
                List.of("Không chen lấn xô đẩy trên cầu thang bộ", "Không quay trở lại nhà lấy đồ quý giá khi chưa được phép"),
                "Mẹo sống còn: Sau động đất, thang máy có thể bị kẹt hoặc đứt cáp bất cứ lúc nào; chỉ dùng cầu thang bộ."
        ));

        DisasterGuideDto eqGuide = new DisasterGuideDto(
                "earthquake",
                "06",
                "ĐỘNG ĐẤT",
                "Che chắn đầu và cổ",
                "Che chắn và đề phòng dư chấn",
                "Khi xảy ra rung chấn địa chấn, giữ bình tĩnh và thực hiện quy tắc 'Cúi - Che - Giữ' sẽ cứu sống bạn khỏi các mảnh vỡ trần nhà rơi xuống.",
                "Hạ thấp người xuống gầm bàn kiên cố, bảo vệ đầu và cổ; sau khi rung chấn dứt thì sơ tán bằng cầu thang bộ ra bãi đất trống.",
                "fa-solid fa-house-crack",
                "topic-blue",
                List.of(
                        "Nếu đang ở trong nhà, cúi thấp, che đầu và cổ; bám giữ dưới bàn chắc chắn nếu có thể.",
                        "Tránh cửa kính, tủ cao và vật có thể rơi; không dùng thang máy trong hoặc ngay sau rung chấn.",
                        "Sau rung chấn, di chuyển đến nơi an toàn khi có thể và đề phòng dư chấn.",
                        "Làm theo hướng dẫn của lực lượng chức năng."
                ),
                eqSteps,
                List.of(
                        "Nắm vững thao tác Drop - Cover - Hold on",
                        "Xác định được chiếc bàn kiên cố nhất trong nhà",
                        "Đã chỉ dẫn cho các thành viên vị trí cầu thang bộ thoát hiểm",
                        "Biết vị trí bãi đất trống gần nhà nhất"
                ),
                "Nếu bị mắc kẹt dưới đống đổ nát, hãy dùng một mảnh đá gõ đều đặn vào ống nước hoặc tường bê tông để đội cứu hộ dò tìm sóng âm thanh."
        );
        guideMap.put("earthquake", eqGuide);

        // 07. SAU THIÊN TAI VÀ PHỤC HỒI
        List<DisasterStepDto> afterSteps = new ArrayList<>();
        afterSteps.add(buildStep(
                1,
                "Kiểm tra cẩn trọng rủi ro điện giật và rò rỉ khí gas",
                "Chỉ bước vào nhà khi có xác nhận an toàn từ chính quyền và lực lượng cứu hộ",
                "Sau bão lũ, hiểm họa ngầm từ dây điện hở dưới bùn đất và rò rỉ khí gas tích tụ trong phòng kín là nguyên nhân gây ra nhiều vụ tai nạn thương tâm.",
                "after-step1.jpg",
                "Chủ nhà đeo ủng và găng tay bảo hộ kiểm tra kỹ lưỡng đường dây điện và van gas trước khi vào nhà (Hình ảnh AI minh họa)",
                List.of(
                        "Đi ủng cao su và đeo găng tay bảo hộ khi tiếp cận nhà bị ngập.",
                        "Ngửi xem có mùi gas thoang thoảng không; nếu có, lập tức mở toang cửa sổ và không bật bất kỳ công tắc điện nào.",
                        "Nhờ thợ điện kiểm tra độ cách điện của hệ thống dây âm tường trước khi đóng cầu dao điện trở lại.",
                        "Kiểm tra các vết nứt trên tường nhà, trần nhà hoặc dầm cột chịu lực."
                ),
                List.of("Dùng đèn pin kiểm tra các góc tối, đề phòng rắn rết hoặc côn trùng độc bò vào nhà tránh nước", "Chụp ảnh ghi nhận lại toàn bộ hiện trường thiệt hại"),
                List.of("Không bật diêm, quẹt lửa hoặc hút thuốc khi vào nhà vừa rút nước", "Không tự ý nối lại dây điện ngoài trời bị đứt"),
                "Mẹo sống còn: Một tia lửa nhỏ từ bật công tắc đèn cũng có thể kích nổ khối khí gas rò rỉ tích tụ trong nhà kín!"
        ));

        afterSteps.add(buildStep(
                2,
                "Khử trùng nước sinh hoạt và vệ sinh môi trường",
                "Ăn chín uống sôi, dùng Cloramin B và vôi bột để ngăn chặn dịch bệnh bùng phát",
                "Nước lũ rút đi để lại lớp bùn vi khuẩn ô nhiễm nặng nề. Xử lý triệt để nguồn nước và dọn dẹp vệ sinh phòng dịch là ưu tiên số một để bảo vệ sức khỏe gia đình.",
                "after-step2.jpg",
                "Đun sôi nước uống tiệt trùng, dùng viên khử khuẩn và phun xịt thuốc sát trùng quanh nhà (Hình ảnh AI minh họa)",
                List.of(
                        "Làm trong nước ngập bằng phèn chua (1g phèn cho 20 lít nước) trước khi khử trùng bằng viên Cloramin B.",
                        "Đun sôi nước ít nhất 5-10 phút trước khi dùng để uống và nấu ăn.",
                        "Vứt bỏ toàn bộ thực phẩm đã bị tiếp xúc với nước ngập bẩn, kể cả đồ đông lạnh bị rã đông lâu ngày.",
                        "Thu gom xác động vật chết chôn sâu có rắc vôi bột; quét dọn bùn đất và phun thuốc diệt muỗi, ruồi."
                ),
                List.of("Rửa tay thường xuyên bằng xà phòng sát khuẩn", "Đưa người có biểu hiện sốt, tiêu chảy đến trạm y tế ngay"),
                List.of("Tuyệt đối không uống nước lã chưa qua xử lý tiệt trùng", "Không để bùn rác ứ đọng quanh khu vực sinh hoạt"),
                "Mẹo sống còn: Dịch tả và sốt xuất huyết thường bùng phát từ 3 đến 7 ngày sau lũ; giữ vệ sinh cá nhân là lá chắn thép bảo vệ bạn."
        ));

        DisasterGuideDto afterGuide = new DisasterGuideDto(
                "after",
                "07",
                "SAU THIÊN TAI",
                "Chỉ trở về khi đã an toàn",
                "Trở về và vệ sinh an toàn",
                "Khi thiên tai qua đi, giai đoạn dọn dẹp và tái thiết đời sống đòi hỏi sự cẩn trọng cao độ đối với dịch bệnh, hóa chất và nguy cơ sập đổ thứ cấp.",
                "Chỉ hồi hương khi chính quyền cho phép; kiểm tra an toàn điện, gas; tiệt trùng nguồn nước và dọn sạch môi trường sống.",
                "fa-solid fa-hand-holding-medical",
                "topic-green",
                List.of(
                        "Chỉ trở về khi khu vực được xác nhận an toàn.",
                        "Tránh dây điện đứt, công trình hư hỏng và nguồn nước ô nhiễm.",
                        "Kiểm tra sức khỏe, sơ cứu khi cần và tìm hỗ trợ y tế phù hợp.",
                        "Ghi nhận thiệt hại chỉ khi an toàn; không vào vùng nguy hiểm để chụp ảnh.",
                        "Dùng nước sạch, thực phẩm an toàn và giữ vệ sinh môi trường."
                ),
                afterSteps,
                List.of(
                        "Đã kiểm tra an toàn kết cấu và hệ thống điện gas",
                        "Có sẵn ủng, găng tay và Cloramin B xử lý nước",
                        "Chuẩn bị nguồn nước uống đóng chai dự phòng",
                        "Đã tiêm phòng hoặc bổ sung thuốc phòng dịch theo chỉ dẫn y tế"
                ),
                "Hãy theo dõi thông tin từ chính quyền địa phương để cập nhật tình hình tiếp nhận đồ cứu trợ lương thực, thuốc men từ các tổ chức thiện nguyện."
        );
        guideMap.put("after", afterGuide);
    }
}
