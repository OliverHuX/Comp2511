package unsw.skydiving;

public class Instructor extends LicencedJumper {
    private String dropZone;
    public Instructor(String name, String licence, String dropZone) {
        super(name, licence);
        this.dropZone = dropZone;
    }

    public String getDropZone() {
        return dropZone;
    }
}
