package seq;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mpmj.Card;
import mpmj.Combination;

public class ThirdteenYao extends Fan{
	private List<Card> hands;
	private final static String name = "ThirdteenYao";
	private final static int score = 10;
	public ThirdteenYao(List<Card> cards,List<Combination> deskCards){
		super(cards,deskCards,score,name);
		this.hands = cards;
	}
	@Override
	public boolean checkFan() {
		if(hands.size()==14) {
			if(hands.stream().allMatch(c->Arrays.asList(1,9,10,18,19,27,28,29,30,31,32,33,34).contains(c.getRank()))) {
				if(hands.stream().map(Card::getRank).collect(Collectors.toSet()).size()==13) {
					return true;
				}
			}
		}
		return false;
	}
}
