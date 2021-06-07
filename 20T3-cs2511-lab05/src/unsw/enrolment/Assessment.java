package unsw.enrolment;

public interface Assessment {
    public void calculateMark();
    public void addAssessment(Mark assignment);
    public int getMark();
}