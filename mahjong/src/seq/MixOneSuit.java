package seq;

import java.util.List;

import mpmj.Card;
import mpmj.Combination;

public class MixOneSuit extends Fan{
	private List<Card> hands;
	private List<Combination> deskCards;
	private final static String name = "MixOneSuit";
	private final static int score = 3;
	public MixOneSuit(List<Card> cards,List<Combination> deskCards) {
		super(cards,deskCards,score,name);
		this.hands = cards;
		this.deskCards = deskCards;
	}
	@Override
	public int getScore() {
		return 3;
	}
	@Override
	public boolean checkFan() {
		if(hands.stream().anyMatch(c->c.getRank()>27)&&hands.stream().anyMatch(c->c.getRank()<=27)) {
			if(!hands.stream().filter(c->c.getRank()<=27).anyMatch(c->c.getSuit()!=hands.get(0).getSuit())) {
				return true;
			}
		}
		return false;
	}
}
