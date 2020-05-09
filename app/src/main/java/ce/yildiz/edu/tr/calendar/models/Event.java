package ce.yildiz.edu.tr.calendar.models;

public class Event {

    private String title;
    private boolean isAllDay;
    private String date;
    private String time;
    private String month;
    private String year;
    private boolean isNotify;
    private int notificationID;
    private String note;
    private int color;
    private String location;
    private String phoneNumber;
    private String email;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
