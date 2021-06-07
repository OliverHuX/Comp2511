package unsw.enrolment;

public interface Observer {
    public void update(Enrolment enrolment, String grade, String name);
}
