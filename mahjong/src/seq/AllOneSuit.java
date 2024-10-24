package seq;

import java.util.List;

import mpmj.Card;
import mpmj.Combination;

public class AllOneSuit extends Fan{
	private List<Card> hands;
	private List<Combination> deskCards;
	private final static String name = "AllOneSuit";
	private final static int score = 6;
	public AllOneSuit(List<Card> cards,List<Combination> deskCards) {
		super(cards,deskCards,score,name);
		this.hands = cards;
		this.deskCards = deskCards;
	}
	@Override
	public int getScore() {
		return 6;
	}
	@Override
	public boolean checkFan() {
		if(!hands.stream().anyMatch(c->c.getRank()>27)) {
			if(hands.stream().filter(c->c.getSuit()!=hands.get(0).getSuit()).toList().size()==0) {
				return true;
			}
		}
		return false;
	}
}
