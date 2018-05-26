package fr.picedr.bot;

import javax.security.auth.login.LoginException;

import java.util.Calendar;

import fr.picedr.bot.listener.MiscListener;
import fr.picedr.bot.listener.PrivateListener;
import fr.picedr.bot.listener.PublicListener;
import fr.picedr.bot.utils.MsgUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.AccountType;
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
		int previousHour = 0;
		
		
	
		try {

			String botId = System.getProperty("botId");


			JDA  jda;
			
	
		 	// Connect to Discord
	 		jda = new JDABuilder(AccountType.BOT)
			 .setToken(botId)
			 .setBulkDeleteSplittingEnabled(false)
			 .buildBlocking();
			/*
			 * Initiate listeners : 
			 * - PrivateListener : MP to the bot
			 * - PublicListener : for public channels messages
			 * - MiscListener :  others such as connection of users, playng games, ...
			 */
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
				if (hour!=previousHour){
					previousHour = hour;
					
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

}