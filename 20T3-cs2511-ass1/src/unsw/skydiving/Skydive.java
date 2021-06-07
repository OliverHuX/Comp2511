package unsw.skydiving;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Skydive {
    private ArrayList<Skydiver> skydivers;
    private String flightId;
    private String id;
    private String jumpType;
    private String passenger;
    private String trainee;
    private LocalDateTime starttime;
    private LocalDateTime endTime;
    
    public Skydive(String flightId, String id, String jumpType, ArrayList<Skydiver> skydivers, LocalDateTime starttime, LocalDateTime endTime) {
        this.id = id;
        this.jumpType = jumpType;
        this.skydivers = skydivers;
        this.flightId = flightId;
        this.endTime = endTime;
        this.starttime = starttime;
    }

    public ArrayList<Skydiver> getSkydivers() {
        return skydivers;
    }

    public LocalDateTime getStartTime() {
        return starttime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getId() {
        return id;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getType() {
        return jumpType;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

    public void setTrainee(String trainee) {
        this.trainee = trainee;
    }

    public String getPassenger() {
        return passenger;
    }

    public String getTrainee() {
        return trainee;
    }

    @Override
    public String toString() {
        return "jump type : " + jumpType + "skydivers : " + skydivers.size();
    }

}
