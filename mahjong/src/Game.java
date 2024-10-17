
import java.util.List;

public class Game {
	private Deck deck;
	private List<Player> pList;
	private Card droppedCard;
	public Game(Deck deck,List<Player> pList){
		this.deck = deck;
		this.pList = pList;
		droppedCard=null;
	}
	public Card getDroppedCard() {
		return droppedCard;
	}
	public void setDroppedCatd(Card c) {
		this.droppedCard = c;
	}
	
	public void initiate() {
		while(deck.getRemainingCard()>0) {
			
		}
	}
}
