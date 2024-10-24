package mpmj;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


public class Deck {
	List<Card> cards;
	List<Player> players;
	private Card dropCard;
	Scanner	in = new Scanner(System.in);
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
			Collections.shuffle(cards);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public int getRemainingCard() {
		return cards.size();
	}
	public void addCard(Player p,int rank) throws Exception{
		List<Card.Value> values = Card.getNumbers();
		if(rank>27){
			switch(rank){
				case 32:
				p.draw(new Card(Card.Suit.中, Card.Value.中));
				break;
				case 33:
				p.draw(new Card(Card.Suit.發, Card.Value.發));
				break;
				case 34:
				p.draw(new Card(Card.Suit.白, Card.Value.白));
				break;
				case 28:
				p.draw(new Card(Card.Suit.風, Card.Value.東));
				break;
				case 29:
				p.draw(new Card(Card.Suit.風, Card.Value.南));
				break;
				case 30:
				p.draw(new Card(Card.Suit.風, Card.Value.西));
				break;
				case 31:
				p.draw(new Card(Card.Suit.風, Card.Value.北));
				break;
			}
		}
		else if(rank>=19){
			Card.Suit suit = Card.Suit.筒;
			p.draw(new Card(suit,values.get(rank%19)));
		}
		else if(rank>=10){
			Card.Suit suit = Card.Suit.索;
			p.draw(new Card(suit,values.get(rank%10)));
		}
		else if(rank>=1){
			Card.Suit suit = Card.Suit.萬;
			p.draw(new Card(suit,values.get(rank-1)));
		}
		else{
			throw new Exception("Invalid rank.");
		}
	}
	public synchronized void giveCard(boolean debug) throws Exception {
		if(debug){
			System.out.println("Please input card by rank line by line:");
			for(Player p:players){
				String cards = in.nextLine();
				for(String i:cards.split(",")){
					int rank = Integer.parseInt(i);
					addCard(p, rank);
				}
			}
		}
		else{
			for(Player p:players) {
				for(int i =0;i<13;i++) {
					p.draw(cards.remove(0));
				}
			}
		}
	}
	
	public synchronized Card draw(Player p) {
		dropCard=null;
		Card card = cards.remove(0);
		p.draw(card);
		return card;
	}

	public List<Action> checkAction(Player playerOfRound) {
		List<Action> actions = new ArrayList<>();
		for(Player p:players) {
			if(p==playerOfRound)
				continue;
			else {
				if(p.canHu(dropCard)) {
					List<Object> temp = new ArrayList<>();
					temp.add(p.getHandCards());
					temp.add(p.getDeskCards());
					actions.add(new Action(Action.Type.HU, p,-1,null,false,temp));
				}
				if(p.canPong(dropCard))
					actions.add(new Action(Action.Type.PONG, p,-1,null,false,null));
				if(p.canKong(dropCard))
					actions.add(new Action(Action.Type.KONG, p,-1,null,false,null));
				if(p.getPlayerId()==(playerOfRound.getPlayerId()+1)%4) {
					if(p.canChow(dropCard).size()>0)
						actions.add(new Action(Action.Type.CHOW, p,-1,p.canChow(dropCard),false,null));
				}
			}
		}
		return actions;
	}

	public void setDroppCard(Card tempCard) {
		dropCard = tempCard;
	}

	public Card getDropCard() {
		return dropCard;
	}

	public void listAllPlayerCard() {
		for(Player p:players) {
			System.out.println(p.getName()+"'s card: "+p.listAllCards());
		}
	}

	public List<Action> checkDrawAction(Card draw,Player p) {
		List<Action> actions = new ArrayList<>();
		if(p.canMingKong(draw)){
			actions.add(new Action(Action.Type.MINGKONG, p, -1, null,true,null));
		}
		if(p.canUmKong()){
			actions.add(new Action(Action.Type.UMKONG, p, -1, null,true,null));
		}
		if(p.canHu(null)){
			List<Object> temp = new ArrayList<>();
			temp.add(p.getHandCards());
			temp.add(p.getDeskCards());
			actions.add(new Action(Action.Type.ZIMO, p, -1, null,true,temp));
		}
		return actions;
	}

}