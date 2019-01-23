package fr.picedr.bot.jeux.quizz;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import fr.picedr.bot.Bot;
import fr.picedr.bot.Params;
import fr.picedr.bot.jeux.quizz.bean.QuestionQuizz;
import fr.picedr.bot.utils.MsgUtils;
import fr.picedr.bot.utils.UserUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.List;


public class GenerateListThread extends Thread{

	private Logger logger = LoggerFactory.getLogger(GenerateListThread.class);

	private Guild server;
	
	GenerateListThread(Guild server){
		this.server = server;
	}
	
	
	public void run(){
		try {
			logger.debug("run - start : server=<"+server.getName()+">");
			QuizzDAO quizzDAO = new QuizzDAO();
			Hashtable<Integer, List<String>> pendingQ = quizzDAO.getState(server.getId(),1);
	        Hashtable<Integer, List<String>> answerdQ = quizzDAO.getState(server.getId(),2);
	        
	        List<QuestionQuizz> questions= quizzDAO.getAllQuestions(server.getId());
	        
	        try {
	            File file = new File("quizz.pdf");
	            FileOutputStream fop = new FileOutputStream(file);
	            Document document = new Document();
	            PdfWriter writer = PdfWriter.getInstance(document, fop);
	            
	            document.open();
	            PdfContentByte cb = writer.getDirectContent();
	            
	            PdfPTable table = new PdfPTable(3);
	            table.setWidthPercentage(95);
	            table.setWidths(new float[] { 3, 1, 1 });
	            
	            Font fontTitle = FontFactory.getFont("Open Sans", 10,Font.BOLD,BaseColor.WHITE);
	            
	            Font fontQ = FontFactory.getFont("Open Sans", 6,BaseColor.BLACK);
	            Font fontU = FontFactory.getFont("Open Sans", 8,BaseColor.BLACK);
	            
	            
	            PdfPCell et1 = new PdfPCell(new Phrase("Question",fontTitle));
	            et1.setVerticalAlignment(Element.ALIGN_MIDDLE);
	            et1.setBackgroundColor(BaseColor.BLACK);

	            PdfPCell et2 = new PdfPCell(new Phrase("En cours",fontTitle));
	            et2.setVerticalAlignment(Element.ALIGN_MIDDLE);
	            et2.setBackgroundColor(BaseColor.BLACK);

	            PdfPCell et3 = new PdfPCell(new Phrase("R�pondus",fontTitle));
	            et3.setVerticalAlignment(Element.ALIGN_MIDDLE);
	            et3.setBackgroundColor(BaseColor.BLACK);        
	            
	            
	            table.addCell(et1);
	            table.addCell(et2);
	            table.addCell(et3);	            
	            
	            int row = 0;
				

				for (QuestionQuizz qq:questions){
					String question  = "";
					question = question +  "ID : "+qq.getId()+
							"\nCLE : "+qq.getKey()+
							"\nQUESTION : "+qq.getQuestion().replace("##", "\n")+
							"\nREPONSE : "+qq.getAswer()+
							"\nRETOUR : "+qq.getRetour();
					
					String pu = "";
					if (pendingQ.containsKey(Integer.valueOf(qq.getId()))){
						List<String> pul = pendingQ.get(Integer.valueOf(qq.getId()));
						for (int i = 0;i<pul.size();i++){
							if (i>0){
								pu = pu.concat("\n");
							}
							pu = pu.concat(UserUtils.getUserById(server,pul.get(i)).getName());
							
						}
						
					}
					
					String au = "";
					if (answerdQ.containsKey(Integer.valueOf(qq.getId()))){
						List<String> aul = answerdQ.get(Integer.valueOf(qq.getId()));
						for (int i = 0;i<aul.size();i++){
							if (i>0){
								au = au.concat("\n");
							}
							au = au.concat(UserUtils.getUserById(server,aul.get(i)).getName());
							
						}
						
					}    
					
					
					PdfPCell c1 = new PdfPCell(new Phrase(question,fontQ));
					c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					
					PdfPCell c2 = new PdfPCell(new Phrase(pu,fontU));
					c2.setVerticalAlignment(Element.ALIGN_MIDDLE);
					
					PdfPCell c3 = new PdfPCell(new Phrase(au,fontU));
					c3.setVerticalAlignment(Element.ALIGN_MIDDLE);
					
					
					if (row ==0){
						c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
						c2.setBackgroundColor(BaseColor.LIGHT_GRAY);    
						c3.setBackgroundColor(BaseColor.LIGHT_GRAY);        
						row = 1;
					}else {
						c1.setBackgroundColor(BaseColor.WHITE);
						c2.setBackgroundColor(BaseColor.WHITE);    
						c3.setBackgroundColor(BaseColor.WHITE);    
						row = 0;
					}
					
					table.addCell(c1);
					table.addCell(c2);
					table.addCell(c3);

	                
	            }            
	            
	            
	            document.add(table);
	            document.close();
	            
				TextChannel adminChannel = server.getTextChannelById(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL));
				MsgUtils.sendFile(adminChannel, file,"Voici la liste des questions existantes :");
				
				file.delete();
	            
	        } catch (FileNotFoundException ex) {
	        	logger.error("File not found",ex);
	        } catch (DocumentException ex) {
				logger.error("Problem generating document",ex);
	        }
		} catch (Exception e) {
			logger.error("General problem",e);
		}
		logger.debug("run - end");
	}    

}
