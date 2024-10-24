package seq;

import java.util.List;

import mpmj.Card;
import mpmj.Combination;

public abstract class Fan {
	private List<Card> hands;
	private List<Combination> deskCards;
	private int score;
	private String fanName;
	public Fan(List<Card> cards,List<Combination> deskCards,int score,String fanName) {
		this.hands = cards;
		this.deskCards = deskCards;
		this.score = score;
		this.fanName = fanName;
	}
	public int getScore() {
		return score;
	};
	public abstract boolean checkFan();
	@Override
	public String toString(){
		return fanName;
	}
}
