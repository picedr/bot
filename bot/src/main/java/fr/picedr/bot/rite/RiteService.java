package fr.picedr.bot.rite;

import fr.picedr.bot.Bot;
import fr.picedr.bot.BotService;
import fr.picedr.bot.Params;
import fr.picedr.bot.rite.bean.Ritualist;
import fr.picedr.bot.utils.MsgUtils;
import fr.picedr.bot.utils.UserUtils;
import fr.picedr.bot.utils.Utils;
import net.dv8tion.jda.core.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RiteService implements BotService {

    private String MSG_STEP1 = "Ho bonjour jeune noble, je vois que les officiers ont jugés que tu étais à même de te lancer dans notre rite initiatique. Je serai là pour t'apporter toutes les informations nécessaires, après tout, c'est mon histoire. :smile:Ton épreuve de mise en bouche sera de résoudre l'énigme d'un de nos anciens maîtres de guilde, Miloh Olgku. Il est aussi espiègle qu'ingénieux. \n"
            + "La voici donc :\n "
            + "**\"Dans l'autre sens, j'effacerai tout sur mon passage, mais heureusement pour vous j'ai choisi le sens dans lequel je suis.\"**\n"
            + "Quand tu penseras avoir trouvé la réponse, il te faudra juste me parler avec la commande ***!rite <taréponse>*** et je te répondrai si c'est la bonne. :wink:";
    private String MSG_STEP2 = "Toutes mes félicitations jeune noble, tu as trouvé la bonne réponse ! Te voilà lancé sur le jeu de piste qui va te conter mon parcours Eorzéen, te faire suivre mes pas, mes doutes, mes douleurs et surtout mes joies. Tout se déroulera de cette façon, je te donnerai une citation de mon recueil de voyage pour que tu puisses trouver l'étape requise.\n"
            + " Voici la citation :\n "
            + "\"Celle qui fût était forte et ses enfants tout autant, suffisamment indomptables et fiers\n"
            + "Qu'ils ont relevé de la poussière et du sable une partie de son ampleur, Petite.\n"
            + "A ses pieds, la pierre enjambe un reptile laissé trop longtemps face à la morsure de la chaleur\n"
            + "Pour s'abimer dans un dédale de braseros vers la révélation finale.\"\n"
            + "C'est un peu nébuleux mais mon esprit n'a jamais été un paysage très clair... :wink:\n"
            + "Quand tu auras trouvé la réponse, parle moi avec la commande ***!rite 00x00y*** et je t'expliquerai la raison de cette étape ainsi que la citation suivante.\n"
            + "Je te souhaite bon courage mon jeune enfant.";
    private String MSG_STEP3 = "Cette partie symbolise mon errance de fondateur jusqu'aux abîmes de la solitude. Qui a fini par me révéler sa vraie valeur de mémoire et de connaissance, telle la statue tenant cette immense amphore. Amphore représentant notre guilde, son savoir, son vécu, ses valeurs. Tu peux parler au **3ème classe** au fond de la salle pour continuer ton voyage.\n"
            + "La nouvelle citation se présente ainsi :\n"
            + "\"Hôte-toi ! Car l'air en fusion que tu respires ne pardonne pas tes faux pas sur le sable.\n"
            + "Au loin, les restes d'une civilisation perdue depuis fort longtemps, peuplée de Déchus.\n"
            + "Bien que la mort règne en ces lieux, un a trouvé la force d'embrasser tout de même la vie.\"\n"
            + "Comme pour l'autre, n'oublie pas de me contacter avec la commande ***!rite 00x00y*** et l'on pourra continuer notre petite voyage. :smile:";
    private String MSG_STEP4 = "Ici, tu viens de vivre ce que j'ai dû vivre pour comprendre que même au travers du souvenir de ma guilde et mes amis disparus, de ma propre mort annoncée dans ce maudit désert, je pouvais trouver au moins une personne pour croire en la vie, malgré tout...\n"
            + "Alors, alors, la prochaine citation prendra place dans une contrée plus iodée :\n"
            + "\"Sous la bienveillante garde d'une cantatrice oc�anique, trône un pilier.\n"
            + "Il fut lumière, puis rien, puis cristal, incapable de distinguer la terre sombre que foulent ses pieds.\n"
            + "Passé le cimetière de fer et de bois se trouve un rescapé atypique proposant une lumière éclatante.\"\n"
            + "N'oublie pas que pour continuer, il te faudra me contacter avec la commande ***!rite 00x00y***. ";
    private String MSG_STEP5 = "Mon chemin de la l'ombre à la lumière donne l'ampleur aux humeurs et périples de la guilde au travers de sa construction et de sa consolidation. Suite à la découverte de mon successeur à un endroit où je n'aurais jamais pensé le trouver... Mais alors vraiment ...\n"
            + "Ha oui, la citation suivante, l'une de mes préférées :\n"
            + "\"Il y a un lac où des arbres poussent à l'envers. Ou seraient-ce les arbres qui portent la terre?\n"
            + "Sur le chemin du sable se tapit une communauté particulière, une communauté de sans-visage.\n"
            + "Prête à frapper à tous moments de faiblesse, elle abrite pourtant une lumière guidant dans les ténèbres.\"\n"
            + "Penses à me contacter avec la commande ***!rite 00x00y*** !";
    private String MSG_STEP6 = "Cette étape, dont je ne suis pas forcément très fier, montre la décrépitude qui prit peu à peu place dans mon esprit. J'oubliais peu à peu le visage des gens qui m'entouraient, nos rencontres. Mais savais que malgré tout, l'espoir survivrait pour mon chef d'oeuvre...\n"
            + "Enfin, la quiétude de ce passage :\n"
            + "\"Des idiots naissent des flots sombres louvoyant sous les restes des Titans de jadis.\n"
            + "En prenant la direction de la merveille flottant sur l'éther, celle qui fit la fierté des mages de guerre,\n"
            + "se profile un sentier vers le repos bien mérité, le dernier reposé\"\n"
            + "La commande ***!rite 00x00y***, cette fois-ci, se révèlera salvatrice... ";
    private String MSG_STEP7 = "Et voici la fin de notre histoire *kof kof*, celle de ma retraite, moi votre fondateur. :smile:J'ai la gorge s�che d'avoir autant parl�. Ce ne m'arrive gu�re ces derniers temps. Et c'est aussi la fin de ce jeu de piste. Félicitations ! Il ne reste plus qu'à entrer dans le vif du sujet. Les véritables épreuves du rite ... *sourire sadique*";
    private String MSG_NOP="Hahaha!! \nEssaie encore petit scarabé.";


    private String MSG_SOL1 = "mog";
    private String MSG_SOL2 = "12x22y";
    private String MSG_SOL3 = "24x40y";
    private String MSG_SOL4 = "12x36y";
    private String MSG_SOL5 = "14x33y";
    private String MSG_SOL6 = "15x09y";

    public static String MSG_HELP_JEUNE="Houla, jeune noble, pour pouvoir te permettre ce genre de chose il te faut posséder le bon rang. Rapproche toi d'un officier pour en savoir plus. Mais bon ... Pas trop, certains sont \"spéciaux\"";
    public static String MSG_HELP_VIEUX="Dit donc petit samouraï, tu n'as plus besoin de moi pour ça. Alors laisse les jeunes faire leurs preuves ! :smiley:";





    private String SITUATION1_MES = "Situation 1 : Quand les chats sont pas là ...\n"+
            "Ce sont enfin les vacances en guilde, le chef doit s'absenter pour une semaine, YOUPI !!! \n"
            + "Pendant cette semaine les officiers décident aussi de souffler (marre de gérer tous ces guildoux-noob "
            + "toujours à râler). Du coup un certain laisser-aller s'installe dans la maison de guilde, les armes "
            + "d'entrainement se prom�nent partout, l'écurie doit se sentir jusqu'à Gridania, les bouteilles vides "
            + "s'entassent sur les tables etc à Le dimanche soir arrive vite et le retour du chef approche !!!!!!\n "
            + "Tu te rends compte que les 3eme et 4eme guerres mondiales ont eu lieu dans la maison. Que faire ?????";

    private String SITUATION1_L = "A - Tu chope les 2 prochains guildoux qui se connectent et les forces aux travaux de nettoyages.\n"+
            "B - Tu essayes de mettre la main sur un officier avant le retour du chef pour lui refiler le problème.\n"+
            "C - Tu rappelles tous les connectés à la maison pour un nettoyage collectif.\n"+
            "D - Tu fermes les yeux. Le chef verra bien en rentrant.\n"
            + "Répond par A, B, C ou D";

    private String SITUATION1_C = "1 - Tu joues à fond sur ton rôle de gradis et continue de faire faire le boulot aux autres.\n"+
            "2 - Tu embètes tous les officiers il y en aura bien un qui finira par répondre.\n"+
            "3 - C'est sympa de faire le ménage tous ensemble au final. Ca fait une bonne ambiance.\n"+
            "4 - Tu déco n'y vu n'y connu.\n"
            + "Répond 1, 2, 3 ou 4";


    private String SITUATION1_R2 = "Les officiers et les guildiens sont très déçus de ton comportement. Perdu";

    private String SITUATION1_R1 = "Les officiers et les guildiens sont très déçus de ton comportement. Perdu";

    private String SITUATION1_R3 = "Bonne solution, tout le monde s'amuse au final. Gagné";

    private String SITUATION1_R4 = "Le chef est furax après tout le monde, tu aurais dû agir. Perdu";



    private String SITUATION2_MES = "Situation 2 : l'accident !\n"+
            "Rotorr, de retour de vacances, décide de faire un barbecue dans le jardin de la guilde."
            + " La fête bat son plein, ça danse, ça rigole, etc ... Tout à coup, tu entends un cri : c'est Kallyn,"
            + " les invités ont écrasés les fleurs du potager !!!! Elle n'est vraiment pas contente.\n"
            + "A toi de gérer la situation.";

    private String SITUATION2_L = "A - Tu prends le partie de Kallyn (ce n'est pas bien faut faire attention).\n"+
            "B - Tu prends le partie de Roro (ce ne sont que des fleurs il y a pas morts d'hommes).\n"+
            "C - Tu aides Roro à réparer les catastrophes pour calmer Kallyn.\n"+
            "D - Tu t'en fous, tu fais comme si tu n'avais rien vu.\n"+
            "Répond par A, B, C ou D";

    private String SITUATION2_C = "1 Gros calins à Kallyn pour faire oublier tout �a.\n"+
            "2 - Tu changes d'avis et te ranges de l'autre côté.\n"+
            "3 - Tu maintiens ton choix et réprimande le second.\n"+
            "4 - Tu déco pour avoir la paix.\n"
            + "Répond 1, 2, 3 ou 4";

    private String SITUATION2_R2 = " La dispute s'envenime, tout le monde fini vexé, roro est faché, Kallyn pleure. Perdu";

    private String SITUATION2_R3 = " La dispute s'envenime, tout le monde fini vexé, roro est faché, Kallyn pleure. Perdu";

    private String SITUATION2_R1 = "Bonne solution, tout le monde est calmé et content. Gagné";

    private String SITUATION2_R4 = "Tout le monde se fait la tête, mauvaise ambiance, tu aurais dû agir. Perdu";



    private String SITUATION3_MES = "Situation 3 : la bagarre !\n"+
            "Fifle et Miloh, après avoir vid�é la cave à vin fraichement installée, donc bourrés, remontent "
            + "dans le salon très très très énervés (le vin ça rend mauvais c'est connu). Une dispute a éclaté"
            + " entre eux au sujet du sport. Ils menacent tous les deux de déguilder si tu ne les aides pas."
            +"\nQue fais-tu ?";

    private String SITUATION3_L = "A - Tu soutiens Filfe : le rugby c'est nul vive le foot.\n"+
            "B - Tu soutiens Miloh : le foot c'est des guignols qui courent après une baballe. Vive le rugby.\n"+
            "C -  Tu tentes la diplomatie sans prendre parti et les envois dormir.\n"+
            "D - Tu vas voir s'ils n�ont pas laissé trainer une bouteille pour te bourrer la gueule toi aussi.\n"+
            "Répond par A, B, C ou D";

    private String SITUATION3_C = "1 Tu maintiens ton avis.\n"+
            "2 Tu changes d'avis l'autre a de bons arguments.\n"+
            "3 La discussion te fatigue tu vas te bourrer aussi.\n"+
            "4 Tu changes de méthode et tu tentes d�apaiser les mecs.\n"
            + "Répond 1, 2, 3 ou 4";

    private String SITUATION3_R1 = "La dispute s'envenime, la bagarre éclate, ils déguildent. Perdu";

    private String SITUATION3_R2 = "La dispute s'envenime, la bagarre éclate, ils déguildent. Perdu";

    private String SITUATION3_R4 = "Bonne solution, ils vont dormir et demain corvée de nettoyage. Gagné";

    private String SITUATION3_R3 = "Vous étiez tous bourr�s et vous avez tous les 3 déguildés. Perdu";

    private String ROLE_RITE = "331046685218439178";
    private String ROLE_SHOGUN="330605723048345600";
    private String ROLE_SHISHO="330605818590527490";
    private String ROLE_SAMOURAI="330605907610435584";
    private String CHAN_SHISHO="330614043574272002";
    private String CHAN_GENERAL="320838482886918145";


    private Logger logger = LoggerFactory.getLogger(RiteService.class);

    private static RiteService INSTANCE = null;

    List<String> exception;

    private RiteService(){
        this.exception = new ArrayList<>();
        exception.add("371004791511449607");
    }

    public static RiteService getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new RiteService();
        }
        return INSTANCE;
    }

    /**
     * Dispatch the command passed to the service
     *
     * @param server  : Server from which come the command
     * @param channel : Channel from which come the commande
     * @param user    : user that launch the command
     * @param cmd     : command
     * @param content : parameters of the command
     */
    public void dispatch(Guild server, TextChannel channel, Message msg, User user, String cmd, String content) {
        logger.debug("dispatch - start : server=<" + server.getName() + "> - channel=<" + channel.getName() + "> - user=<" + user.getName() + "> - cmd=<" + cmd + "> - content = <" + content + ">");


        if (server.getId().equals("320838482886918145")) {
            logger.debug("Service papy is up for this server");
            switch (cmd) {
                case "!rite":
                    etat(server,channel,msg);
                    break;
                default:

            }
        }
    }

    /**
     * Called to display help
     *
     * @param channel : channel to display help on
     * @param server  : server which called the help
     */
    public static void help(Guild server, TextChannel channel) {
        List<String> tell = new ArrayList<>();


        if (server.getId().equals("320838482886918145")) {
            tell.add("AIDE POUR LA SECTION [RITE]");


                /*if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {

                }*/
            MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS);
        }

    }



    public void runRite(User user, String content){

        List<Role> roles = user.getMutualGuilds().get(0).getMember(user).getRoles();
        boolean riteAvailable = false;
        boolean ritePassed = false;

        for (Role role : roles){
            String roleID = role.getId();
            if (roleID.equals(ROLE_RITE)){
                riteAvailable = true;
            }else if ((roleID.equals(ROLE_SAMOURAI)||roleID.equals(ROLE_SHISHO)
                    ||roleID.equals(ROLE_SHOGUN))&&(!exception.contains(user.getId()))){
                ritePassed = true;
            }
        }

        logger.debug("**Rite** : *runRite* - riteAvailable :<"+riteAvailable+"> - ritePassed : <"+ritePassed+"> ");
        if (riteAvailable && !ritePassed){
            String param = content.toLowerCase()
                    .replaceAll("!rite", "").trim();
            logger.debug("**Rite** : *runRite* - param : <"+param+"> ");
            if (param.equals("")){
                repeat(user);
            }else if (param.equals("lalafel")){
                step1(user);
            }else if(param.equals("mog")){
                step2(user);
            }else if(this.checkCoord(param,"12x22y")){
                step3(user);
            }else if(this.checkCoord(param,"24x40y")){
                step4(user);
            }else if(this.checkCoord(param,"12x36y")){
                step5(user);
            }else if(this.checkCoord(param,"14x33y")){
                step6(user);
            }else if(this.checkCoord(param,"15x09y")){
                step7(user);
            }else{
                MsgUtils.tell(user, MSG_NOP);
            }



        }else {
            MsgUtils.tell(user, RiteService.MSG_HELP_JEUNE);
        }
        logger.debug("**Rite** : *runRite* - Fin");
    }

    private void step1(User user){
        logger.debug("**Rite** : *step1* - Debut");
        logger.debug("**Rite** : *step1* - id = "+user.getName());
        RiteDAO riteDao = new RiteDAO();
        Ritualist r = riteDao.getRitualist(user.getId());
        TextChannel chanShisho = Bot.getInstance().getJDA().getTextChannelById(CHAN_SHISHO);
        if (r==null){
            MsgUtils.tell(chanShisho, user.getName()+" a démarré le rite.");
            MsgUtils.tell(user, MSG_STEP1,Params.DEFAUL_TYPING_DELAY);
            riteDao.addRitualist(user.getId());
        } else if (r.getState()==1){
            repeat(user);
        } else {
            MsgUtils.tell(user,"Nop",Params.DEFAUL_TYPING_DELAY);
        }
        logger.debug("**Rite** : *step1* - Fin");
    }

    private void step2(User user){
        logger.debug("**Rite** : *step2* - Debut");
        logger.debug("**Rite** : *step2* - id = "+user.getName());
        RiteDAO riteDao = new RiteDAO();
        Ritualist r = riteDao.getRitualist(user.getId());
        TextChannel chanShisho = Bot.getInstance().getJDA().getTextChannelById(CHAN_SHISHO);

        if (r!=null){
            if (r.getState()==1){
                MsgUtils.tell(chanShisho, user.getName()+" a répondu à l'énigme du mog et passe à l'étape suivante.");
                MsgUtils.tell(user, MSG_STEP2,Params.DEFAUL_TYPING_DELAY);
                riteDao.setState(user.getId(),2);
            } else if (r.getState()==2){
                repeat(user);
            } else {
                MsgUtils.tell(user,"Nop",Params.DEFAUL_TYPING_DELAY);
            }
        } else {
            MsgUtils.tell(user,"Nop",Params.DEFAUL_TYPING_DELAY);
        }
        logger.debug("**Rite** : *step2* - Fin");
    }


    private void step3(User user){
        logger.debug("**Rite** : *step3* - Debut");
        logger.debug("**Rite** : *step3* - id = "+user.getName());
        RiteDAO riteDao = new RiteDAO();
        Ritualist r = riteDao.getRitualist(user.getId());
        TextChannel chanShisho = Bot.getInstance().getJDA().getTextChannelById(CHAN_SHISHO);

        if (r!=null){
            if (r.getState()==2){
                MsgUtils.tell(chanShisho, user.getName()+" a trouvé les coordonnées 12x22y (premier lieu) et passe à l'étape suivante.");
                MsgUtils.tell(user, MSG_STEP3,Params.DEFAUL_TYPING_DELAY);
            } else if (r.getState()==3){
                repeat(user);
            } else {
                MsgUtils.tell(user,"Nop",Params.DEFAUL_TYPING_DELAY);
            }
        } else {
            MsgUtils.tell(user,"Nop",Params.DEFAUL_TYPING_DELAY);
        }
        logger.debug("**Rite** : *step3* - Fin");
    }

    private void step4(User user){
        logger.debug("**Rite** : *step4* - Debut");
        logger.debug("**Rite** : *step4* - id = "+user.getName());
        RiteDAO riteDao = new RiteDAO();
        Ritualist r = riteDao.getRitualist(user.getId());
        TextChannel chanShisho = Bot.getInstance().getJDA().getTextChannelById(CHAN_SHISHO);

        if (r!=null){
            if (r.getState()==3){
                MsgUtils.tell(chanShisho,user.getName()+" a trouvé les coordonnées 24x40y (deuxième lieu) et passe à l'étape suivante.");
                MsgUtils.tell(user, MSG_STEP4,Params.DEFAUL_TYPING_DELAY);
            } else if (r.getState()==4){
                repeat(user);
            } else {
                MsgUtils.tell(user,"Nop",Params.DEFAUL_TYPING_DELAY);
            }
        } else {
            MsgUtils.tell(user,"Nop",Params.DEFAUL_TYPING_DELAY);
        }
        logger.debug("**Rite** : *step4* - Fin");
    }

    private void step5(User user){
        logger.debug("**Rite** : *step5* - Debut");
        logger.debug("**Rite** : *step5* - id = "+user.getName());
        RiteDAO riteDao = new RiteDAO();
        Ritualist r = riteDao.getRitualist(user.getId());
        TextChannel chanShisho = Bot.getInstance().getJDA().getTextChannelById(CHAN_SHISHO);

        if (r!=null){
            if (r.getState()==4){
                MsgUtils.tell(chanShisho, user.getName()+" a trouvé les coordonnées 12x36y (troisième lieu) et passe à l'étape suivante.");
                MsgUtils.tell(user, MSG_STEP5,Params.DEFAUL_TYPING_DELAY);
            } else if (r.getState()==5){
                repeat(user);
            } else {
                MsgUtils.tell(user,"Nop",Params.DEFAUL_TYPING_DELAY);
            }
        } else {
            MsgUtils.tell(user,"Nop",Params.DEFAUL_TYPING_DELAY);
        }
        logger.debug("**Rite** : *step5* - Fin");
    }

    private void step6(User user){
        logger.debug("**Rite** : *step6* - Debut");
        logger.debug("**Rite** : *step6* - id = "+user.getName());
        RiteDAO riteDao = new RiteDAO();
        Ritualist r = riteDao.getRitualist(user.getId());
        TextChannel chanShisho = Bot.getInstance().getJDA().getTextChannelById(CHAN_SHISHO);

        if (r!=null){
            if (r.getState()==5){
                MsgUtils.tell(chanShisho, user.getName()+" a trouvé les coordonnées 14x33y (quatrième lieu) et passe à l'étape suivante.");
                MsgUtils.tell(user, MSG_STEP6,Params.DEFAUL_TYPING_DELAY);
            } else if (r.getState()==6){
                repeat(user);
            } else {
                MsgUtils.tell(user,"Nop",Params.DEFAUL_TYPING_DELAY);
            }
        } else {
            MsgUtils.tell(user,"Nop",Params.DEFAUL_TYPING_DELAY);
        }
        logger.debug("**Rite** : *step6 - Fin*");
    }

    private void step7(User user){
        logger.debug("**Rite** : *step7* - Debut");
        logger.debug("**Rite** : *step7* - id = "+user.getName());
        RiteDAO riteDao = new RiteDAO();
        Ritualist r = riteDao.getRitualist(user.getId());
        TextChannel chanShisho = Bot.getInstance().getJDA().getTextChannelById(CHAN_SHISHO);

        if (r!=null){
            if (r.getState()==6){
                MsgUtils.tell(chanShisho, user.getName()+" a trouvé les coordonnées 15x9y (dernier lieu) et fini ainsi le jeu de piste.");
                MsgUtils.tell(user, MSG_STEP7,Params.DEFAUL_TYPING_DELAY);
            } else if (r.getState()==7){
                repeat(user);
            } else {
                MsgUtils.tell(user,"Nop",Params.DEFAUL_TYPING_DELAY);
            }
        } else {
            MsgUtils.tell(user,"Nop",Params.DEFAUL_TYPING_DELAY);
        }
        logger.debug("**Rite** : *step7* - Fin");
    }

    private void repeat(User user){
        logger.debug("**Rite** : *repeat* - Debut");
        RiteDAO riteDao = new RiteDAO();
        Ritualist r = riteDao.getRitualist(user.getId());
        if (r != null) {
            if (r.getState()==1) {
                MsgUtils.tell(user, MSG_STEP1);
            } else if (r.getState()==2) {
                MsgUtils.tell(user, MSG_STEP2);
            } else if (r.getState()==3) {
                MsgUtils.tell(user, MSG_STEP3);
            } else if (r.getState()==4) {
                MsgUtils.tell(user, MSG_STEP4);
            } else if (r.getState()==5) {
                MsgUtils.tell(user, MSG_STEP5);
            } else if (r.getState()==6) {
                MsgUtils.tell(user, MSG_STEP6);
            } else if (r.getState()==7) {
                MsgUtils.tell(user, MSG_STEP7);
            }
        }
        logger.debug("**Rite** : *repeat* - Fin");
    }


    private boolean checkCoord(String uCoord, String vCoord){
        logger.debug("**Rite** : *checkCoord* - Debut");

        boolean result = false;
        String checkX = uCoord.substring(2, 3);
        String checkY = uCoord.substring(5, 6);
        String uValX = uCoord.substring(0,2);
        String uValY = uCoord.substring(3,5);
        String vValX = vCoord.substring(0,2);
        String vValY = vCoord.substring(3,5);
        logger.debug("**Rite** : *checkCoord* - checkX=<"+checkX+">,checkY=<"+checkY+">");
        logger.debug("**Rite** : *checkCoord* - uValX=<"+uValX+">,uValY=<"+uValY+">");
        logger.debug("**Rite** : *checkCoord* - vValX=<"+vValX+">,vValY=<"+vValY+">");

        boolean notNumber = false;

        int vx = 0;
        int vy = 0;
        int ux = 0;
        int uy = 0;

        try {
            vx = new Integer(vValX);
            vy = new Integer(vValY);
            ux = new Integer(uValX);
            uy = new Integer(uValY);
        } catch (NumberFormatException e) {
            logger.debug("Not a number");
            notNumber = true;
        }

        logger.debug("**Rite** : *checkCoord* - vx=<"+vx+">,vy=<"+vy+">");
        logger.debug("**Rite** : *checkCoord* - ux=<"+ux+">,uy=<"+uy+">");

        if (uCoord.length()!=6 || !checkX.toLowerCase().equals("x") || !checkY.toLowerCase().equals("y") || notNumber){
            logger.debug("Bad coord Format");
        }else{
            boolean validX = false;
            boolean validY = false;

            if (ux==vx || ux == vx+1 || ux == vx-1 ){
                validX = true;
            }
            if (uy==vy || uy == vy+1 || uy == vy-1 ){
                validY = true;
            }

            logger.debug("**Rite** : *checkCoord* - validX=<"+validX+">,validY=<"+validY+">");

            if (validX && validY){
                result = true;
            }
        }

        logger.debug("**Rite** : *checkCoord* - Fin");
        return result;
    }




    private void etat(Guild server, TextChannel channel,Message msg){
        logger.debug("**Rite** : *etat* - Debut");
        if (channel.getId().equals(CHAN_SHISHO)) {
            RiteDAO riteDao = new RiteDAO();
            List<Ritualist> ritualists = riteDao.getRitualists();
            List<String> tell = new ArrayList<>();
            ritualists.stream().forEach(e -> tell.add(UserUtils.getUserById(server,e.getUserId()) + " : étape "+e.getState()));
            MsgUtils.tellBlockFramed(channel, tell,MsgUtils.FT_CSS);
        }else{
            msg.delete().complete();
        }
        logger.debug("**Rite** : *etat* - Fin");
    }


    public void situation (User user, String message){

        logger.debug("**Rite** : *situation* - Debut");
        logger.debug("**Rite** : *situation* - message = <"+message+">");
        logger.debug("**Rite** : *situation* - user = <"+user.getName()+">");
        RiteDAO riteDao = new RiteDAO();
        Ritualist r = riteDao.getRitualist(user.getId());
        boolean go = false;
        if (r!=null){
            if (r.getState()==1){
                if (message.equals("!situation")){
                    if (r.getLastHelp()==null){
                        go = true;
                    }else {
                        Date lastH  = r.getLastHelp();
                        Date today = new Date();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(lastH);
                        cal.add(Calendar.DAY_OF_MONTH, 7);
                        Date testDate = cal.getTime();
                        if (testDate.before(today)){
                            go = true;
                        }else{
                            MsgUtils.tell(user, "Tu dois attendre une semaine entre deux aides.");
                        }
                    }
                    logger.debug("**Rite** : *situation* - go = <"+go+">");
                    if (go){
                        int rand = Utils.rand(3);
                        logger.debug("**Rite** : *situation* - rand = <"+rand+">");
                        switch (rand) {
                            case 0:
                                MsgUtils.tell(user, SITUATION1_MES);
                                MsgUtils.tell(user, SITUATION1_L);
                                break;
                            case 1:
                                MsgUtils.tell(user, SITUATION2_MES);
                                MsgUtils.tell(user, SITUATION2_L);
                                break;
                            case 3:
                                MsgUtils.tell(user, SITUATION3_MES);
                                MsgUtils.tell(user, SITUATION3_L);
                                break;
                            default:
                                break;
                        }

                        riteDao.setHelp(user.getId(),"L"+rand);
                        logger.debug("**Rite** : *situation* - helpStateSit = <L"+rand+">");
                    }
                } else if (message.equals("A")
                        ||message.equals("B")
                        ||message.equals("C")
                        ||message.equals("D")) {
                    if (r.getHelp()!=null && r.getHelp().startsWith("L")){

                        switch (r.getHelp()) {
                            case "L0":
                                MsgUtils.tell(user, SITUATION1_C);
                                riteDao.setHelp(user.getId(),"C0");
                                break;
                            case "L1":
                                MsgUtils.tell(user, SITUATION2_C);
                                riteDao.setHelp(user.getId(),"C1");
                                break;
                            case "L2":
                                MsgUtils.tell(user, SITUATION3_C);
                                riteDao.setHelp(user.getId(),"C2");
                                break;
                            default:
                                break;
                        }
                    }
                }else if (message.equals("1")
                        ||message.equals("2")
                        ||message.equals("3")
                        ||message.equals("4")) {
                    if (r.getHelp()!=null && r.getHelp().startsWith("C")){
                        boolean ok = false;

                        switch (r.getHelp()) {
                            case "C0":
                                if (message.equals("1")){
                                    MsgUtils.tell(user, SITUATION1_R1);
                                }else if (message.equals("2")){
                                    MsgUtils.tell(user, SITUATION1_R2);
                                }else if (message.equals("3")){
                                    MsgUtils.tell(user, SITUATION1_R3);
                                    ok = true;
                                }else if (message.equals("4")){
                                    MsgUtils.tell(user, SITUATION1_R4);
                                }
                                break;
                            case "C1":
                                if (message.equals("1")){
                                    MsgUtils.tell(user, SITUATION2_R1);
                                    ok = true;
                                }else if (message.equals("2")){
                                    MsgUtils.tell(user, SITUATION2_R2);
                                }else if (message.equals("3")){
                                    MsgUtils.tell(user, SITUATION2_R3);
                                }else if (message.equals("4")){
                                    MsgUtils.tell(user, SITUATION2_R4);
                                }
                                break;
                            case "C2":
                                if (message.equals("1")){
                                    MsgUtils.tell(user, SITUATION3_R1);
                                }else if (message.equals("2")){
                                    MsgUtils.tell(user, SITUATION3_R2);
                                }else if (message.equals("3")){
                                    MsgUtils.tell(user, SITUATION3_R3);
                                }else if (message.equals("4")){
                                    MsgUtils.tell(user, SITUATION3_R4);
                                    ok = true;
                                }
                                break;
                            default:
                                break;
                        }
                        logger.debug("**Rite** : *situation* - ok = <"+ok+">");
                        if (ok){
                            if (r.getLastHelp()==null){
                                MsgUtils.tell(user, "La première lettre de la réponse est M");
                            }else {
                                MsgUtils.tell(user, "La dernière lettre de la réponse est G");
                            }

                            if (r.getLastHelp()==null){
                                riteDao.helped(user.getId());
                            }
                        }
                    }
                }
            }else {
                MsgUtils.tell(user, "Cette commande est indisponible � cette �tape.");
            }


        }else{
            MsgUtils.tell(user, "C'est encore un peu t�t pour toi.");
        }

        logger.debug("**Rite** : *situation* - Fin");
    }




}
