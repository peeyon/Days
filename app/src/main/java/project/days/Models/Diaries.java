package project.days.Models;

public class Diaries {
    public String text;

    public Diaries() {}

    public Diaries(String text) {
        this.text = text;
    }

    public String getDiary_name() {
        return text;
    }

    public void setDiary_name(String text) {
        this.text = text;
    }
}
