package fr.picedr.bot.agenda.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {

    private int id;
    private Date date;
    private String content;
    private List<Event> rappels;
    private String serverId;
    private String type;

    public Event(int id, Date date, String content, List<Event> rappels, String serverId, String type){
        this.id = id;
        this.date = date;
        this.content = content;
        this.rappels = rappels;
        this.serverId = serverId;
        this.type = type;
    }

    public Event(int id, Date date, String content, String serverId, String type){
        this.id = id;
        this.date = date;
        this.content = content;
        this.rappels = new ArrayList<>();
        this.serverId = serverId;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void addRappel(Event rappel){
        this.rappels.add(rappel);
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Event> getRappels() {
        return rappels;
    }

    public void setRappels(List<Event> rappels) {
        this.rappels = rappels;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
