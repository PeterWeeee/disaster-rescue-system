package vn.edu.cuunan.dto;

import java.util.List;

public class EquipmentDispatchDto {

    private List<DispatchItem> items;
    private String notes;

    public EquipmentDispatchDto() {
    }

    public static class DispatchItem {
        private Long equipmentId;
        private int quantity;

        public DispatchItem() {
        }

        public DispatchItem(Long equipmentId, int quantity) {
            this.equipmentId = equipmentId;
            this.quantity = quantity;
        }

        public Long getEquipmentId() {
            return equipmentId;
        }

        public void setEquipmentId(Long equipmentId) {
            this.equipmentId = equipmentId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    public List<DispatchItem> getItems() {
        return items;
    }

    public void setItems(List<DispatchItem> items) {
        this.items = items;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
