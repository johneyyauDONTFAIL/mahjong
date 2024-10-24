package seq;

import java.util.ArrayList;
import java.util.List;

import mpmj.Card;
import mpmj.Combination;

public class PongPongHu extends Fan{
	private List<Card> hands;
	private List<Combination> deskCards;
	private final static String name = "PongPongHu";
	private final static int score = 3;
	public PongPongHu(List<Card> cards,List<Combination> deskCards) {
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
		int kind = 0;
		int pair= 0;
		List<Card> temp = new ArrayList<>(hands);
		for(Card c:hands) {
			if(hands.stream().filter(card->card.getRank()==c.getRank()).count()>=3) {
				kind++;
				temp.removeIf(card->card.getRank()==c.getRank());
			}
			if(hands.stream().filter(card->card.getRank()==c.getRank()).count()==2) {
				pair++;
				temp.removeIf(card->card.getRank()==c.getRank());
			}
		}
		if(kind==hands.size()/3 && pair==1) {
			if(temp.isEmpty())
				return true;
		}
		return false;
	}
}
