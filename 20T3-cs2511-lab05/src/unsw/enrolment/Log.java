package unsw.enrolment;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalTime;

public class Log implements Observer {

    @Override
    public void update(Enrolment enrolment, String grade, String name) {
        try {
            String fileName = enrolment.getCourse().getCourseCode() +  "-" + enrolment.getTerm() + "-" + enrolment.getZid();
            File file = new File(fileName);
            String content = LocalDate.now() + " " + LocalTime.now() + " " + grade + " " + name + " " + enrolment.getMark() + "\n";
            FileWriter f = new FileWriter(file, true);
            f.write(content);
            f.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
    }
}
