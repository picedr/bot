package fr.picedr.bot;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Params {

    /*
    BOT ROLES
     */
    public static String SUPERADMIN="superadmin";

    /*
    SERVER CONF
     */
    public static String CONF_ADMINCHANNEL = "adminchannel";
    public static String CONF_GENERALCHANNEL = "generalchannel";
    public static String CONF_ADMINROLE = "adminrole";

    public static List<String> SERVER_CONF;
    static{
        SERVER_CONF = new ArrayList<>();
        SERVER_CONF.add(CONF_ADMINCHANNEL);
        SERVER_CONF.add(CONF_ADMINROLE);
        SERVER_CONF.add(CONF_GENERALCHANNEL);
    }

    /*
    SERVER SERVICES
     */

    public static String SRV_TELL = "tell";
    public static String SRV_MUTE = "mute";

    public static Hashtable<String, String> SRV_DESC;
    static {
        SRV_DESC = new Hashtable<>();
        SRV_DESC.put(SRV_TELL, "Permet de parler au nom du bot");
        SRV_DESC.put(SRV_MUTE, "Permet de bloquer tout message écrit sur un chan");

    }





}
