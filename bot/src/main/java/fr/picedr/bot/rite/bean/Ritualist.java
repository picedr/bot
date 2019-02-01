package fr.picedr.bot.rite.bean;

import java.util.Date;

public class Ritualist {

    private String userId;
    private int state;
    private Date lastHelp;
    private String help;

    public Ritualist(String userId, int state){
        this.userId = userId;
        this.state = state;
    }


    public String getUserId() {
        return userId;
    }

    public int getState() {
        return state;
    }

    public Date getLastHelp() {
        return lastHelp;
    }

    public void setLastHelp(Date lastHelp) {
        this.lastHelp = lastHelp;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }
}
