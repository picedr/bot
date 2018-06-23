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
    public static String CONF_VOICEROLE = "voicerole";
    public static String CONF_SHAMECHANNEL = "shamechannel";

    public static List<String> SERVER_CONF;
    static{
        SERVER_CONF = new ArrayList<>();
        SERVER_CONF.add(CONF_ADMINCHANNEL);
        SERVER_CONF.add(CONF_ADMINROLE);
        SERVER_CONF.add(CONF_GENERALCHANNEL);
        SERVER_CONF.add(CONF_VOICEROLE);
        SERVER_CONF.add(CONF_SHAMECHANNEL);
    }

    /*
    SERVER SERVICES
     */

    public static String SRV_TELL = "tell";
    public static String SRV_MUTE = "mute";
    public static String SRV_SHAME = "shame";
    public static String SRV_SLOW = "slow";
    public static String SRV_CLEAR = "clear";
    public static String SRV_AGENDA = "agenda";
    public static String SRV_ANNIV="anniv";

    public static Hashtable<String, String> SRV_DESC;
    static {
        SRV_DESC = new Hashtable<>();
        SRV_DESC.put(SRV_TELL, "Permet de parler au nom du bot");
        SRV_DESC.put(SRV_MUTE, "Permet de bloquer les messages publics venant d'un utilisateur");
        SRV_DESC.put(SRV_SHAME,"Banni un utilisateur sur un voice chan à part");
        SRV_DESC.put(SRV_SLOW,"Permet de ralentir le rythme de message sur un chan");
        SRV_DESC.put(SRV_CLEAR,"Permet d'effacer **x** messages sur un chan");
        SRV_DESC.put(SRV_AGENDA,"Gestion d'évenements avec rappels");
        SRV_DESC.put(SRV_ANNIV,"Gestion des anniversaires");
    }





}
