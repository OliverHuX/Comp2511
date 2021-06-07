package unsw.enrolment;

import java.util.ArrayList;
import java.util.List;

public class Enrolment {

    private CourseOffering offering;
    private Grade grade;
    private Student student;
    private List<Session> sessions;
    private ArrayList<Observer> observers;

    public Enrolment(CourseOffering offering, Student student, Session... sessions) {
        this.offering = offering;
        this.student = student;
        this.grade = null; // Student has not completed course yet.
        this.observers = new ArrayList<Observer>();
        student.addEnrolment(this);
        offering.addEnrolment(this);
        this.sessions = new ArrayList<>();
        for (Session session : sessions) {
            this.sessions.add(session);
        }
    }

    public Course getCourse() {
        return offering.getCourse();
    }

    public String getTerm() {
        return offering.getTerm();
    }

    public boolean hasPassed() {
        return grade != null && grade.isPassing();
    }

    public void setGrade(Grade grade, String name){
        this.grade = grade;
        notifyObserve(grade.getGrade(), name);
    }

    public String getGrade() {
        return grade.getGrade();
    }

    public int getMark() {
        return grade.getMark();
    }

    public String getZid() {
        return student.getZID();
    }

    public void addObserve(Observer o) {
        observers.add(o);
    }

    public void notifyObserve(String grade, String name) {
        for(Observer observer: observers) {
            observer.update(this, grade, name);
        }
    }

    public void deleteObserve(Observer o) {
        observers.remove(o);
    }

//    Whole course marks can no longer be assigned this way.
//    public void assignMark(int mark) {
//        grade = new Grade(mark);
//    }

}
