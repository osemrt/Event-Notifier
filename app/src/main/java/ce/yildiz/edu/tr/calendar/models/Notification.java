package ce.yildiz.edu.tr.calendar.models;

public class Notification {

    private int id;
    private int eventId;
    private int channelId;
    private String time;

    public Notification(int eventId, int channelId, String time) {
        this.eventId = eventId;
        this.channelId = channelId;
        this.time = time;
    }

    public Notification(String time) {
        this.time = time;
    }

    public Notification() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
