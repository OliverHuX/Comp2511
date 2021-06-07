package unsw.skydiving;

import java.time.LocalDateTime;

public class TimePeriod {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TimePeriod(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

}
