package vn.edu.cuunan.entity;

public enum VerificationStatus {
    PENDING("Chờ xác minh", "#64748b"),
    VERIFIED("✓ Đã xác minh", "#16a34a"),
    REJECTED("Bác bỏ / Sai tin", "#dc2626");

    private final String label;
    private final String color;

    VerificationStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }
}
