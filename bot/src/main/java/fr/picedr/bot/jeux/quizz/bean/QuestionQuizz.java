package fr.picedr.bot.jeux.quizz.bean;

public class QuestionQuizz {
	
	private String id;
	private String key;
	private String question;
	private String answer;
	private String retour;
	
	public QuestionQuizz(String id, String key, String question, String answer){
		this.id = id;
		this.key = key;
		this.question = question;
		this.answer = answer;
		this.retour = "";
	}
	
	public QuestionQuizz(String id, String key, String question, String answer, String retour){
		this.id = id;
		this.key = key;
		this.question = question;
		this.answer = answer;
		this.retour = retour;
	}	
	
	public boolean checkAnswer(String prop){
		boolean result = false;
		if (prop.equals(answer)){
			result = true;
		}
		return result;
	}
	
	public String getQuestion(){
		return question;
	}
	
	public String toString(){
		String result = "";
		if (hasRetour()){
			result =  id+"@@"+key+"@@"+question+"@@"+answer+"@@"+retour;
		}else {
			result =  id+"@@"+key+"@@"+question+"@@"+answer;
		}
		return result;
	}
	
	public String getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public String getAswer() {
		return answer;
	}
	
	public String getRetour() {
		return retour;
	}
	
	public boolean hasRetour(){
		boolean result = false;
		if (retour!= null && !retour.equals("")){
			result = true;
		}
		return result;
	}
}
