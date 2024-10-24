package seq;

import java.util.List;

import mpmj.Card;
import mpmj.Combination;

public class CommonHu extends Fan {
	private List<Card> hands;
	private List<Combination> deskCards;
	private final static String name = "CommonHu";
	private final static int score = 1;
	public CommonHu(List<Card> cards,List<Combination> deskCards) {
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
		for(Card c:hands) {
			if(hands.stream().filter(ca->ca.getRank()==c.getRank()).count()>=3) {
				return false;
			}
		}
		if(deskCards.stream().anyMatch(c->c.getType()==Combination.Type.PONG||c.getType()==Combination.Type.KONG)) {
			return false;
		}
		return true;
	}
}
