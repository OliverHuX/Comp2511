package unsw.enrolment;

import java.util.ArrayList;

public class Mark implements Assessment{
    private int mark;
    private String name;
    private ArrayList<Assessment> marks;
    
    public Mark(String name) {
        this.name = name;
        this.marks = new ArrayList<Assessment>();
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    @Override
    public void calculateMark() {
        int total = 0;
        for(Assessment mark: marks) {
            total += mark.getMark();
        }
        this.mark = total/marks.size();
    }

    @Override
    public void addAssessment(Mark assignment){
        marks.add(assignment);
    }

    @Override
    public int getMark() {
        return mark;
    }

}
