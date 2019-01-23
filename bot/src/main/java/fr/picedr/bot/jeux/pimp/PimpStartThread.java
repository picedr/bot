package fr.picedr.bot.jeux.pimp;

import fr.picedr.bot.utils.MsgUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class PimpStartThread extends Thread{
	
	private Guild server;
	private TextChannel channel;
	
	PimpStartThread(Guild server, TextChannel channel){
		this.server = server;
		this.channel = channel;
	}
	
	
	public void run(){
		try {
			MsgUtils.tellFramed(channel, "Une nouvelle partie de pair ou impair vient d'être lancée.\nTapez !pimp pour y participer",MsgUtils.FT_CSS);
			int count = 30;
			PimpService.getInstance().setState(server,1);
			Message msg = MsgUtils.tell(channel, "Début de partie dans : 30");
			while (true){
				Thread.sleep(1000);	
				count --;
				if (count >0){
					msg = msg.editMessage("Début de partie dans : "+count).complete();
				} else {
					msg.editMessage("La partie est démarrée").complete();
					PimpService.getInstance().newSet(server,channel);
					break;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}    

}
