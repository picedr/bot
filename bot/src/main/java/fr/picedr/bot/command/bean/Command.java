package fr.picedr.bot.command.bean;

public class Command {
    private int id;
    private String serverId;
    private String name;
    private String value;
    private boolean isAdmin;

    public Command (int id, String serverId, String name, String value, boolean isAdmin){
        this.id = id;
        this.serverId = serverId;
        this.name = name;
        this.value = value;
        this.isAdmin = isAdmin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
