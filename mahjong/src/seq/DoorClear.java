package seq;

import java.util.List;

import mpmj.Card;
import mpmj.Combination;

public class DoorClear extends Fan {
	private List<Card> hands;
	private List<Combination> deskCards;
	private final static String name = "DoorClear";
	private final static int score = 1;
	public DoorClear(List<Card> cards,List<Combination> deskCards) {
		super(cards,deskCards,score,name);
		this.hands = cards;
		this.deskCards = deskCards;
	}
	@Override
	public int getScore() {
		return 1;
	}
	@Override
	public boolean checkFan() {
		if(hands.size()==14) {
			return true;
		}
		return false;
	}
}
