package vn.edu.cuunan.dto;

import java.util.List;

public class EquipmentReturnDto {

    private List<ReturnItem> items;
    private String reportNotes;

    public EquipmentReturnDto() {
    }

    public static class ReturnItem {
        private Long assignmentEquipmentId; // ID of EquipmentAssignment
        private int intactQuantity;         // Nguyên vẹn -> cộng lại available
        private int damagedQuantity;        // Hư hỏng -> cộng vào damaged
        private int lostQuantity;           // Bị mất
        private int consumedQuantity;       // Tiêu hao (băng gạc, pin, nước...)

        public ReturnItem() {
        }

        public ReturnItem(Long assignmentEquipmentId, int intactQuantity, int damagedQuantity, int lostQuantity, int consumedQuantity) {
            this.assignmentEquipmentId = assignmentEquipmentId;
            this.intactQuantity = intactQuantity;
            this.damagedQuantity = damagedQuantity;
            this.lostQuantity = lostQuantity;
            this.consumedQuantity = consumedQuantity;
        }

        public Long getAssignmentEquipmentId() {
            return assignmentEquipmentId;
        }

        public void setAssignmentEquipmentId(Long assignmentEquipmentId) {
            this.assignmentEquipmentId = assignmentEquipmentId;
        }

        public int getIntactQuantity() {
            return intactQuantity;
        }

        public void setIntactQuantity(int intactQuantity) {
            this.intactQuantity = intactQuantity;
        }

        public int getDamagedQuantity() {
            return damagedQuantity;
        }

        public void setDamagedQuantity(int damagedQuantity) {
            this.damagedQuantity = damagedQuantity;
        }

        public int getLostQuantity() {
            return lostQuantity;
        }

        public void setLostQuantity(int lostQuantity) {
            this.lostQuantity = lostQuantity;
        }

        public int getConsumedQuantity() {
            return consumedQuantity;
        }

        public void setConsumedQuantity(int consumedQuantity) {
            this.consumedQuantity = consumedQuantity;
        }
    }

    public List<ReturnItem> getItems() {
        return items;
    }

    public void setItems(List<ReturnItem> items) {
        this.items = items;
    }

    public String getReportNotes() {
        return reportNotes;
    }

    public void setReportNotes(String reportNotes) {
        this.reportNotes = reportNotes;
    }
}
