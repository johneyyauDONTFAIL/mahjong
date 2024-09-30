import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
	List<Card> handCards;
	List<Card> deskCards;
	String name;
	public Player(String name) {
		handCards = new ArrayList<>();
		deskCards = new ArrayList<>();
		this.name= name;
	}
	public List<Card> getHandCards(){
		return handCards;
	}
	public String getName() {
		return name;
	}
	public void draw(Card c) {
		handCards.add(c);
	}
	public String listCards() {
		String result = "";
		int index[] = {1};
		result += handCards.stream()
				.map(Card::toString)
				.map(s->{
						int i = index[0];
						String t = s.concat(String.format("[%d]",i));
						index[0]++;
						return t;
					})
				.collect(Collectors.joining(","));
		if(deskCards.size()>0) {
			result += " ||| "+deskCards.stream().map(Card::toString).collect(Collectors.joining(","));
		}
		return result;
	}
	public Card drop(int index) throws Exception {
		if(handCards.size()>=index) {
			Card temp = handCards.remove(--index);
	    	System.out.println("Dropped: "+temp.toString());
			return temp;
    	}
		throw new Exception("Invalid Drop");
	}
	public void pong(List<Card> indexList,Card drop) {
		deskCards.addAll(indexList.stream().limit(2).toList());
		deskCards.add(drop);
		Collections.sort(deskCards);
		handCards.removeAll(indexList.stream().limit(2).toList());
	}
	public void gang(List<Card> indexList,Card drop) {
		deskCards.addAll(indexList.stream().limit(3).toList());
		deskCards.add(drop);
		Collections.sort(deskCards);
		handCards.removeAll(indexList.stream().limit(3).toList());
	}
}
