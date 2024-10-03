import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Player {
	List<Card> handCards;
	List<List<Card>> deskCards;
	String name;
	public Player(String name) {
		handCards = new ArrayList<>();
		deskCards = new ArrayList<>();
		this.name= name;
	}
	public List<Card> getHandCards(){
		return handCards;
	}
	public List<List<Card>> getdeskCards(){return deskCards;}
	public List<Card> getAllCards(){
		return Stream.concat(handCards.stream(), deskCards.stream().flatMap(c->c.stream())).toList();
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
			String desktop = "";
			for(List<Card> cList:deskCards){
				String comb = "";
				comb = cList.stream().map(Card::toString).collect(Collectors.joining("-"));
				desktop += String.format("[%s] ",comb);
			}
			result+=" ||| "+desktop;
			//result += " ||| "+deskCards.stream().flatMap(c->c.stream().map(Card::toString)).collect(Collectors.joining(","));
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
		List<Card> temp = new ArrayList<>(indexList);
		temp.add(drop);
		Collections.sort(temp);
		deskCards.add(temp);
		handCards.removeAll(indexList);
	}
	public void gang(List<Card> indexList,Card drop) {
		List<Card> temp = new ArrayList<>(indexList);
		temp.add(drop);
		Collections.sort(temp);
		deskCards.add(temp);
		handCards.removeAll(indexList);
	}
    public void up(List<Card> upList, Card drop) {
		List<Card> temp = new ArrayList<>(upList);
		temp.add(drop);
		Collections.sort(temp);
		deskCards.add(temp);
		handCards.removeAll(upList);
    }
}
