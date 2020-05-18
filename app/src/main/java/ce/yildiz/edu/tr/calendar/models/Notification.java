package ce.yildiz.edu.tr.calendar.models;

public class Notification {

    private String reminderTime;

    public Notification(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }
}
