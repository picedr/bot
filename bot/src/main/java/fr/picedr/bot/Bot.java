package fr.picedr.bot;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import fr.picedr.bot.agenda.AgendaService;
import fr.picedr.bot.dao.BotDAO;
import fr.picedr.bot.exception.DataNotFoundException;
import fr.picedr.bot.listener.MiscListener;
import fr.picedr.bot.listener.PrivateListener;
import fr.picedr.bot.listener.PublicListener;
import fr.picedr.bot.utils.MsgUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BaseBot is the central class of the bot.
 * It contains reference of all functionnalities and methods to interact with Discord
 */

public class Bot {
	
	/*
	 * call stop() method to pass it to true and stop the bot
	 */
	private boolean stop = false;

    private Hashtable<String,List<String>>  roles;
    private Hashtable<String,Hashtable<String,String>>  serversConf;
    private Hashtable<String,Hashtable<String,String>>  services;

	private JDA  jda;

	private Logger logger = LoggerFactory.getLogger(Bot.class);

	private static Bot INSTANCE = null;
	
	private Bot(){
	
	}

	public static Bot getInstance(){
		if (INSTANCE == null){
			INSTANCE = new Bot();
		}
		return INSTANCE;
	}
	
	public void start(){
		logger.info("Start bot");
		int previousHour = -1;
		int previousMin=-1;
		
		
	
		try {


	
		 	// Connect to Discord
	 		jda = new JDABuilder(AccountType.BOT)
			 .setToken(getKey())
			 .setBulkDeleteSplittingEnabled(false)
			 .buildBlocking();
			/*
			 * Initiate listeners : 
			 * - PrivateListener : MP to the bot
			 * - PublicListener : for public channels messages
			 * - MiscListener :  others such as connection of users, playng games, ...
			 */

			this.setConf();
			jda.addEventListener(new PrivateListener());
			jda.addEventListener(new PublicListener());
			jda.addEventListener(new MiscListener());
			jda.getPresence().setGame(null);

			MsgUtils.tell(jda.getTextChannelById("439298603371069448"),"Salut !");
			
			/*
			 *Loop every second :
			 *- to check if stop has been changed to true
			 *- launch timed job 
			 */
			while (!stop){
				Calendar cal = Calendar.getInstance();
		        int hour = cal.get(Calendar.HOUR_OF_DAY);
		        int min = cal.get(Calendar.MINUTE);
		        boolean newH = false;
		        boolean newM = false;
				if (hour!=previousHour){
					previousHour = hour;
					newH = true;
				}
				if (min!=previousMin){
					previousMin = min;
					newM = true;
				}

				if (newH){
					switch (hour){
						case 0:
							for (String key: services.keySet()) {
								if (services.get(key).get(Params.SRV_AGENDA).equals("1")){
									Guild server = jda.getGuildById(key);
									TextChannel chan = jda.getTextChannelById(serversConf.get(key).get(Params.CONF_GENERALCHANNEL));
									AgendaService.getInstance().today(server,chan);
								}

							};
							break;
						default:
					}
				}
				if (newM){
					AgendaService.getInstance().now(cal.getTime());
					if(hour==23 && min==59){
						AgendaService.getInstance().clear(cal.getTime());
					}
				}

				Thread.sleep(1000);				
			}
			
			jda.shutdown();	
		 		

		} catch (Throwable t){
			logger.error("Root",t);
		}

		logger.info("Close bot");

	}

	public void stop(){
	    this.stop = true;
	}

	private String getKey() throws DataNotFoundException {
	    BotDAO botDAO = new BotDAO();
	    return botDAO.getBotKey();
    }

    private void setConf(){
        BotDAO botDAO = new BotDAO();
        this.roles = botDAO.getRoles();
        this.serversConf = botDAO.getServersConf();
        this.services = botDAO.getServersServices();
    }

    public Hashtable<String, List<String>> getRoles() {
        return roles;
    }

    public Hashtable<String, Hashtable<String, String>> getServersConf() {
        return serversConf;
    }

    public Hashtable<String, Hashtable<String, String>> getServices() {
        return services;
    }

    public JDA getJDA() {return this.jda;}

}