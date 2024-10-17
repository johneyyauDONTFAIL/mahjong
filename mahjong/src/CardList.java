
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CardList implements Serializable{
	private static final long serialVersionUID = 519671736651208253L;
	private List<Card> cards = new ArrayList<>();

	public List<Card> getCardsList(){
		return cards;
	}

	public void addCard(Card c) {
		cards.add(c);
		Collections.sort(cards);
	}
	public Card removeCard() {
		return cards.remove(0);
	}	
	public Card drop(int index) {
		Card card = cards.remove(index-1);
		Collections.sort(cards);
		return card;
	}
	public int size() {
		return cards.size();
	}
	public String toString() {
		return  cards.stream()
				.map(Card::toString)
				.collect(Collectors.joining(","));
	}
	public String toStringWithIndex() {
			String result = "";
			int index[] = {1};
			result += cards.stream()
					.map(Card::toString)
					.map(s->{
							int i = index[0];
							String t = s.concat(String.format("[%d]",i));
							index[0]++;
							return t;
						})
					.collect(Collectors.joining(","));
		return result;
	}


	public String toRank() {
		return  cards.stream()
				.map(Card::getRankStr)
				.collect(Collectors.joining(","));
	}

	public void sort() {
		Collections.sort(cards);
	}

}
