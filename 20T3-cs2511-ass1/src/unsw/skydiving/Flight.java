package unsw.skydiving;

import java.time.LocalDateTime;

public class Flight {
    private String dropZone;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxLoad;
    private int capacity;
    private String id;

    public Flight(String dropZone, int capacity, String id, LocalDateTime startTime, LocalDateTime endTime) {
        this.dropZone = dropZone;
        this.capacity = capacity;
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxLoad = capacity;
    }

    public int getMaxLoad() {
        return capacity;
    }

    public String getDropZone() {
        return dropZone;
    }

    public String getId() {
        return id;
    }

    public void load(int num) {
        if (this.capacity >= num) {
            this.capacity -= num;
        }
    }

    public void drop(int num) {
        if(this.maxLoad >= (this.capacity + num)) {
            this.capacity += num;
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
