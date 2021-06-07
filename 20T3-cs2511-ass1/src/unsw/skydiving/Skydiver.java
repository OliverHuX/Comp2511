package unsw.skydiving;

import java.util.ArrayList;

//import java.util.ArrayList;

public class Skydiver {
    private String name;
    private String licence;
    private ArrayList<TimePeriod> periods;
    
    public Skydiver(String name, String licence) {
        this.name = name;
        this.licence = licence;
    }

    public String getName() {
        return name;
    }

    public String getLicenced() {
        return licence;
    }

    public ArrayList<TimePeriod> getPeriods() {
        return periods;
    }

    public void addPeriod(TimePeriod period) {
        //this.periods.add(period);
        this.periods = new ArrayList<TimePeriod>();
        if(periods.size() == 0) {
            this.periods.add(period);
        } else {
            int count = 0;
            for(TimePeriod t: periods) {
                if(period.getStartTime().isBefore(t.getStartTime())) {
                    this.periods.add(count, period);
                    break;
                }
                count++;
            }
            this.periods.add(period);
        }
    }

    public void removePeriod(TimePeriod period) {
        this.periods.remove(period);
    }

    
}
