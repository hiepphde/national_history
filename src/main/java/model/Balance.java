package model;

import java.time.LocalDateTime;

public class Balance {
    private Long id;
    private Long materialId;
    private Double onHand;
    private Double available;
    private Double hold;
    private LocalDateTime updatedTime;

    public Balance() {}

    public Balance(Long materialId, Double onHand, Double available, Double hold, LocalDateTime updatedTime) {
        this.materialId = materialId;
        this.onHand = onHand;
        this.available = available;
        this.hold = hold;
        this.updatedTime = updatedTime;
    }
}
