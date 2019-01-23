package fr.picedr.bot.jeux.pfc.bean;

public class PfcScore {
	private int win;
	private int lose;
	private int draw;
	
	public PfcScore(){
		this.win = 0;
		this.lose = 0;
		this.draw = 0;
	}
	
	public PfcScore(int win, int draw, int lose){
		this.win = win;
		this.lose = lose;
		this.draw = draw;
	}

	public String toString(){
		return this.win+"-"+this.draw+"-"+this.lose;
	}
	
	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getLose() {
		return lose;
	}

	public void setLose(int lose) {
		this.lose = lose;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}
	
	
	
	
}

