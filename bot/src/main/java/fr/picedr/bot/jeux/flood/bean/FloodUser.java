package fr.picedr.bot.jeux.flood.bean;

public class FloodUser {

	private String userId;
	private int score;
	private int niv;
	private long lastMsg;

	public FloodUser(String userId, int niv, int score){
		this.userId = userId;
		this.score = score;
		this.niv = niv;
		lastMsg = 0;
	}
	
	
	public String toString(){
		return userId+"@@"+niv+"@@"+score;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getNiv() {
		return niv;
	}
	
	public void setNiv(int niv) {
		this.niv = niv;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public long getLastMsg() {
		return lastMsg;
	}

	public void setLastMsg(long lastMsg) {
		this.lastMsg = lastMsg;
	}

	
	
}
