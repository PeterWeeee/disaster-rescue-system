package vn.edu.cuunan.util;

import java.util.Locale;

public enum ProvinceRegion {
    ALL("ALL", "🌐 Toàn Miền Trung (11 Tỉnh/Thành)", 16.0544, 108.2022, 7, new String[]{}),
    THANH_HOA("THANH_HOA", "Thanh Hóa", 19.8074, 105.7764, 11, new String[]{"thanh hóa", "thanh hoa", "sông mã", "nghi sơn", "thiệu dương"}),
    NGHE_AN("NGHE_AN", "Nghệ An", 18.6799, 105.6813, 11, new String[]{"nghệ an", "nghe an", "vinh", "sông lam", "kỳ sơn", "hưng nguyên", "hưng lợi", "mường xén"}),
    HA_TINH("HA_TINH", "Hà Tĩnh", 18.2500, 105.9500, 11, new String[]{"hà tĩnh", "ha tinh", "cẩm xuyên", "hương khê", "cẩm duệ", "kẻ gỗ"}),
    QUANG_TRI("QUANG_TRI", "Quảng Trị", 16.8150, 107.1000, 11, new String[]{"quảng trị", "quang tri", "đông hà", "thạch hãn", "hướng hóa", "cửa việt", "gio linh"}),
    THUA_THIEN_HUE("THUA_THIEN_HUE", "Thừa Thiên Huế", 16.4637, 107.5909, 11, new String[]{"thừa thiên huế", "thừa thiên", "huế", "hue", "hương vinh", "hương trà", "sông hương", "tam giang"}),
    DA_NANG("DA_NANG", "Đà Nẵng", 16.0544, 108.2022, 12, new String[]{"đà nẵng", "da nang", "hải châu", "sơn trà", "hòa vang", "liên chiểu", "mẹ suốt", "cầu đỏ", "hòa khánh", "thuận phước", "nguyễn lương bằng"}),
    QUANG_NGAI("QUANG_NGAI", "Quảng Ngãi", 15.1200, 108.7922, 11, new String[]{"quảng ngãi", "quang ngai", "trà khúc", "ba tơ", "bình sơn", "bình đông"}),
    GIA_LAI("GIA_LAI", "Gia Lai", 13.9833, 108.0000, 11, new String[]{"gia lai", "pleiku", "ia grai", "ia dêr"}),
    DAK_LAK("DAK_LAK", "Đắk Lắk", 12.6667, 108.0500, 11, new String[]{"đắk lắk", "dak lak", "đắc lắc", "buôn ma thuột", "ea súp", "krông ana", "krông pắc", "hòa đông"}),
    KHANH_HOA("KHANH_HOA", "Khánh Hòa", 12.2388, 109.1967, 11, new String[]{"khánh hòa", "khanh hoa", "nha trang", "cam ranh", "ngọc hiệp", "cồn ngọc thảo"}),
    LAM_DONG("LAM_DONG", "Lâm Đồng", 11.9404, 108.4583, 11, new String[]{"lâm đồng", "lam dong", "đà lạt", "bảo lộc", "lạc dương", "xã lát"});

    private final String code;
    private final String name;
    private final double centerLat;
    private final double centerLng;
    private final int defaultZoom;
    private final String[] keywords;

    ProvinceRegion(String code, String name, double centerLat, double centerLng, int defaultZoom, String[] keywords) {
        this.code = code;
        this.name = name;
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.defaultZoom = defaultZoom;
        this.keywords = keywords;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public double getCenterLng() {
        return centerLng;
    }

    public int getDefaultZoom() {
        return defaultZoom;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public boolean matches(String text) {
        if (this == ALL) {
            return true;
        }
        if (text == null || text.isBlank()) {
            return false;
        }
        String lower = text.toLowerCase(Locale.ROOT);
        for (String kw : keywords) {
            if (lower.contains(kw)) {
                return true;
            }
        }
        return false;
    }

    public static ProvinceRegion fromCode(String code) {
        if (code == null || code.isBlank()) {
            return ALL;
        }
        for (ProvinceRegion p : values()) {
            if (p.code.equalsIgnoreCase(code.trim())) {
                return p;
            }
        }
        return ALL;
    }
}
