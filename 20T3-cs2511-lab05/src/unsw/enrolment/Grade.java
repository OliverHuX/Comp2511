package unsw.enrolment;

import java.util.ArrayList;

//import org.graalvm.compiler.phases.common.ConditionalEliminationUtil.Marks;

public class Grade implements Assessment{
    private int mark;
    private String grade;
    private ArrayList<Assessment> marks;
    
    public Grade() {
        this.marks = new ArrayList<Assessment>();
        this.grade = null;
    }

    public void updateGrade() {
        if (mark < 50)
            grade = "FL";
        else if (mark < 65)
            grade = "PS";
        else if (mark < 75)
            grade = "DN";
        else
            grade = "HD";
    }

    @Override
    public void calculateMark() {
        this.mark = 0;
        for(Assessment m: marks) {
            this.mark += m.getMark();
        }
    }

    @Override
    public int getMark() {
        return this.mark;
    }

    @Override
    public void addAssessment(Mark assignment){
        marks.add(assignment);
    }

    public String getGrade() {
        return this.grade;
    }

    public boolean isPassing() {
        return mark >= 50;
    }
}
