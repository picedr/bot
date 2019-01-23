package fr.picedr.bot.jeux.quizz.bean;

import java.util.ArrayList;
import java.util.List;



public class UserQuizz {
	
	private String id;
	private List<String> pendingQuestions;
	private List<String> answerdQuestions;
	
	public UserQuizz(String id){
		this.id = id;
		this.pendingQuestions = new ArrayList<>();
		this.answerdQuestions = new ArrayList<>();
	}
	
	public UserQuizz(String id, List<String> pendingQuestions, List<String> answerdQuestions){
		this.id = id;
		this.pendingQuestions = pendingQuestions;
		this.answerdQuestions = answerdQuestions;
	}
	
	public String toString(){
		String result = id+"@@";
		
		for (int i = 0;i< pendingQuestions.size();i++){
			if (i>0){
				result = result + ",";
			}
			result = result + pendingQuestions.get(i);
		}
		
		result = result + "@@";
				
		for (int i = 0;i< answerdQuestions.size();i++){
			if (i>0){
				result = result + ",";
			}
			result = result + answerdQuestions.get(i);
		}
		
		return result;
	}
	
	public void addQuestion(String qId){
		List<String> result = new ArrayList<String>(this.pendingQuestions);
		result.add(qId);
		
		this.pendingQuestions = result;
	}
	
	public boolean isNewQuestion(String qId){
		boolean result = true;
		if (pendingQuestions.contains(qId)||answerdQuestions.contains(qId)){
			result = false;
		}
		return result;
	}
	
	public boolean isPendingQuestion(String qId){
		boolean result = true;
		if (!pendingQuestions.contains(qId)){
			result = false;
		}
		return result;		
	}
	
	public void completQuestion(String qId){
		List<String> temp = new ArrayList<String>();
		for (int i = 0;i<pendingQuestions.size();i++){
			if (!pendingQuestions.get(i).equals(qId)){
				temp.add(pendingQuestions.get(i));
			}
		}		
		pendingQuestions = temp;
		
		List<String> result = new ArrayList<String>(this.answerdQuestions);
		result.add(qId);
		
		this.answerdQuestions = result;
		
	}
	
	public String getScore(){
		return (new Integer(this.answerdQuestions.size())).toString();
	}
	
	public String getId(){
		return this.id;
	}
	
	public void remQuestion(String id){
		List<String> tempList = new ArrayList<String>();
		for (int i = 0;i< this.answerdQuestions.size();i++){
			if (!answerdQuestions.get(i).equals(id)){
				tempList.add(answerdQuestions.get(i));
			}
		}
		this.answerdQuestions = tempList;
		
		List<String> tempList2 = new ArrayList<String>();
		for (int i = 0;i< this.pendingQuestions.size();i++){
			if (!pendingQuestions.get(i).equals(id)){
				tempList.add(pendingQuestions.get(i));
			}
		}
		this.pendingQuestions = tempList2;		
		
	}
	
	public List<String> getPendingQuestions() {
		return pendingQuestions;
	}
	
	public List<String> getAnswerdQuestions() {
		return answerdQuestions;
	}
	
}
