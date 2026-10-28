package vn.edu.cuunan.entity;

public enum HazardType {
    LANDSLIDE("SẠT LỞ", "Sạt lở đất đá / Đồi núi", "#d97706"),
    FLOOD("NGẬP LỤT", "Ngập lụt diện rộng / Nước sông dâng", "#0284c7"),
    ISOLATED("CÔ LẬP", "Cô lập / Chia cắt đường sá", "#e11d48"),
    TREE_FALL("CÂY ĐỔ", "Cây đổ chắn đường / Đứt cáp điện", "#059669"),
    STORM("BÃO LỚN", "Gió lốc / Tốc mái / Bão lớn", "#7c3aed"),
    OTHER("KHÁC", "Hiểm họa khác", "#64748b");

    private final String shortName;
    private final String description;
    private final String badgeColor;

    HazardType(String shortName, String description, String badgeColor) {
        this.shortName = shortName;
        this.description = description;
        this.badgeColor = badgeColor;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    public String getBadgeColor() {
        return badgeColor;
    }
}
