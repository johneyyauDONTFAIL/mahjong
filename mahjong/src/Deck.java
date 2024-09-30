import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Deck {
	List<Card> cards;
	List<Player> players;
	public Deck(List<Player> players) {
		cards = new ArrayList<>();
		this.players = players;
		try {
			for(Card.Suit suit:Card.Suit.values()) {
				for(Card.Value value:Card.Value.values()) {
					if(suit.validValues(value)) {
						for(int j=0;j<4;j++) {
							Card card = new Card(suit,value);
							cards.add(card);
						}
					}
				}
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void shuffle() {
		Collections.shuffle(cards);
	}
	
	public int getRemainingCard() {
		return cards.size();
	}
	
	public void listCards() {
		for(Card card:cards) {
			System.out.print(card.toString()+", ");
		}
	}
	
	public void draw(Player p,boolean msg) {
		Card card  = cards.remove(0);
		if(msg)
			System.out.println("Drawed: "+card.toString()+ "(" +card.getValue().getRank()+ ")");
		p.draw(card);
		Collections.sort(p.getHandCards());
	}
	public void drawLast(Player p,boolean msg) {
		Card card  = cards.remove(cards.size()-1);
		if(msg)
			System.out.println("Drawed: "+card.toString()+ "(" +card.getValue().getRank()+ ")");
		p.draw(card);
		Collections.sort(p.getHandCards());
	}
	
	public void giveCard() {
		for(Player p:players) {
			for(int i =0;i<13;i++) {
				draw(p,false);
			}
		}
	}
	
	public void drop(Player p,int index,Scanner in){
		try{
			Card temp = p.drop(index);
			boolean action = false;
			for(Player o:players) {
				if(o==p)
					continue;
				else if(!action){
					List<Card> pongList = checkPong(o.getHandCards(),temp);
					if(pongList.size()==2) {						
						if(!action) {
							System.out.println(String.format("Player %s can 碰! Do you want to do so? (Y/N)", o.getName()));
							String response = in.next();
							if(response.equalsIgnoreCase("Y")) {
								o.pong(pongList,temp);
								System.out.println("Player "+o.getName()+" PONG!!!!");
								action =true;
							}
						}
					}
					else if(pongList.size()==3){						
						if(!action) {
							System.out.println(String.format("Player %s can 杠! Do you want to do so? (Y/N)", o.getName()));
							String response = in.next();
							if(response.equalsIgnoreCase("Y")) {
								o.gang(pongList,temp);
								drawLast(o,true);
								System.out.println("Player "+o.getName()+" GANG!!!!");
								action =true;
							}
						}
					}
					
//					List<Integer> upList = checkUp(o.getHandCards(),temp);					
//					if(upList.size()>=2) {						
//						if(!action) {
//							System.out.println(String.format("Player %s can 上! Do you want to do so?", o.getName()));
//							String response = in.next();
//							if(response.equalsIgnoreCase("Y")) {
////								o.action(upList,temp);
//								System.out.println("Player "+o.getName()+" UP!!!!");
//								action =true;
//							}
//						}
//					}

					
				}
			}
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	private List<Card> checkPong(List<Card> cards,Card drop) {
		//int num = cards.stream().filter(c->c.getSuit().equals(drop.getSuit())&&c.getValue().equals(drop.getValue())).collect(Collectors.toList()).size();	
		List<Card> result = new ArrayList<>();
//		for(int i=0;i<cards.size();i++){
//			Card tempCard = cards.get(i);
//			if(tempCard.getSuit().equals(drop.getSuit())&&tempCard.getValue().equals(drop.getValue())) {
//				result.add(i);
//			}
//		}
			long cnt = cards.stream().filter(c->c.getRank()==drop.getRank()).count();
			if(cnt>=2) {
				result = cards.stream().filter(c->c.getRank()==drop.getRank()).toList();
			}
			return result;
	}
	
	private List<Integer> checkUp(List<Card> cards,Card drop){
		List<Integer> result = new ArrayList<>();
		Card.Suit dropSuit = drop.getSuit();
		Card.Value dropValue = drop.getValue();
		
		for(int i=0;i<cards.size()-1;i++) {
			List<Card> temp = Arrays.asList(cards.get(i),cards.get(i+1),drop);
			if(isLink(temp)) {
				result.add(i);
			}
		}
		return result;
	}
	
	private boolean isLink(List<Card> cards) {
		if(cards.get(0).getSuit().equals(cards.get(1).getSuit()) && cards.get(1).getSuit().equals(cards.get(2).getSuit())) {
			Collections.sort(cards);
			if(cards.get(0).getRank()>27) return false;
			if(cards.get(2).getRank()-cards.get(1).getRank() == 1 &&
					cards.get(1).getRank()-cards.get(0).getRank() == 1) {
				return true;
			}
		}
		return false;
	}
	
//	private boolean checkHu(List<Card> cards,Card drop) {
//		
//	}
	
}
