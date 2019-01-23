package fr.picedr.bot.jeux.pimp;

import java.util.Enumeration;
import java.util.Hashtable;

import fr.picedr.bot.utils.MsgUtils;
import fr.picedr.bot.utils.UserUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class PimpSetThread extends Thread{
	
	private TextChannel channel;
	private Guild server;
	
	PimpSetThread(Guild server, TextChannel channel){
		this.channel = channel;
		this.server = server;
	}
	
	
	public void run(){
		try {
			MsgUtils.tellFramed(channel, "Une nouvelle manche de pair ou impair vient d'être lancée.\nTapez en <MP> à [Papy] !pair ou !impair",MsgUtils.FT_CSS);
			Hashtable<String,String> players = PimpService.getInstance().getPlayers().get(server.getId());
			Enumeration<String> keys = players.keys();
			
			while (keys.hasMoreElements()){
				String key = keys.nextElement();
				if (!key.equals("Papy") && !players.get(key).equals("3")){
					MsgUtils.tell(UserUtils.getUserById(server,key), "Tu peux jouer pour cette manche.\nJoue **!pair** ou **!impair**.");
				}
			}
			
			int count = 20;
			PimpService.getInstance().setState(server,2);
			Message msg = MsgUtils.tell(channel, "Fin de la manche dans : 20");
			while (true){
				Thread.sleep(1000);	
				count --;
				if (count >0){
					msg = msg.editMessage("Fin de la manche dans : "+count).complete();
				} else {
					msg.editMessage("La manche  est terminée").complete();
					PimpService.getInstance().endSet(server, channel);
					break;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}    

}
