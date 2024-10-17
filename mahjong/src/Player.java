
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Player implements Serializable{

	private static final long serialVersionUID = 2739625210479513761L;
	private List<Card> handCards;
	private List<Combination> deskCards;
	private String name;
	private int playerId;
	public Player(String name) {
		this.handCards = new ArrayList<>();
		this.deskCards = new ArrayList<>();
		this.name= name;
	}
	public List<Card> getHandCards(){
		return this.handCards;
	}
	public List<Combination> getDeskCards(){return deskCards;}
	// public List<Card> getAllCards(){
	// 	return Stream.concat(handCards.stream(), deskCards.stream().flatMap(c->c.stream())).toList();
	// }
	public String getName() {
		return name;
	}
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int i) {
		this.playerId=i;
	}
//	public void setHandCard(List<Card> cards) {
//		this.handCards = cards;
//	}
	public void draw(Card c) {
		handCards.add(c);
		Collections.sort(handCards);
	}
	public void draw(String cardLine) {
		List<Card.Value> values = Card.getNumbers();
		try {
			for(String rankStr:cardLine.split(",")) {
				int rank = Integer.parseInt(rankStr);
				if(rank>27){
					switch(rank){
						case 32:
							draw(new Card(Card.Suit.中, Card.Value.中));
							break;
						case 33:
							draw(new Card(Card.Suit.發, Card.Value.發));
							break;
						case 34:
							draw(new Card(Card.Suit.白, Card.Value.白));
							break;
						case 28:
							draw(new Card(Card.Suit.風, Card.Value.東));
							break;
						case 29:
							draw(new Card(Card.Suit.風, Card.Value.南));
							break;
						case 30:
							draw(new Card(Card.Suit.風, Card.Value.西));
							break;
						case 31:
							draw(new Card(Card.Suit.風, Card.Value.北));
							break;
					}
				}
				else if(rank>=19){
					Card.Suit suit = Card.Suit.筒;
					draw(new Card(suit,values.get(rank%19)));
				}
				else if(rank>=10){
					Card.Suit suit = Card.Suit.索;
					draw(new Card(suit,values.get(rank%10)));
				}
				else if(rank>=1){
					Card.Suit suit = Card.Suit.萬;
					draw(new Card(suit,values.get(rank-1)));
				}
				else{
					throw new Exception("Invalid rank.");
				}
			}
			Collections.sort(handCards);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Card drop(int index) {
		Card temp = handCards.remove(index);
		Collections.sort(handCards);
		return temp;
	}
	@Override
	public String toString() {
		return "Player name:"+this.name+"|Player Id:"+this.getPlayerId();
	}
	
	public boolean canPong(Card drop) {
		return handCards.stream().filter(c->c.getRank()==drop.getRank()).count()>=2;
	}
	
	public boolean canKong(Card drop) {
		return handCards.stream().filter(c->c.getRank()==drop.getRank()).count()>=3;
	}
	
	public List<List<Card>> canChow(Card drop) {
		if(drop.getRank()>27) {
			return new ArrayList<>();
		}
		else{
			List<Card> possibleCard = handCards.stream()
					.filter(c->c.getSuit()==drop.getSuit())
					.filter(c->Math.abs(c.getRank()-drop.getRank())<=2)
					.filter(c->c.getRank()!=drop.getRank()).toList();
			List<List<Card>> possibleComb = new ArrayList<>();

			for(int i=0;i<possibleCard.size()-1;i++){
				for(int j=i+1;j<possibleCard.size();j++){
					if(Math.abs(possibleCard.get(i).getRank()-possibleCard.get(j).getRank())<=2&&Math.abs(possibleCard.get(i).getRank()-possibleCard.get(j).getRank())>0){
						possibleComb.add(Arrays.asList(possibleCard.get(i),possibleCard.get(j)));
					}
				}
			}
			return possibleComb;
		}
	}
	public boolean canHu(Card drop) {
		List<Card> tempCard = new ArrayList<>(handCards);
		if(drop!=null)
			tempCard.add(drop);
		int[] cardsArr = new int[35];
		for(Card c:tempCard){
			cardsArr[c.getRank()]++;
		}
		return huHelper(cardsArr,0,0,handCards.size()/3);
	}
	private boolean huHelper(int[] card,int pairCount, int tripletCount,int targetTripletCount) {
        if(pairCount==1 && tripletCount==targetTripletCount){
            return true;
        }
        for(int i=0;i<card.length;i++){
            if(card[i]==0){
                continue;
            }

            if(card[i]>=3){
                card[i]-=3;
                if(huHelper(card,pairCount,tripletCount+1,targetTripletCount)){
                    return true;
                }
                card[i]+=3;
            }
            if(pairCount==0){
                if(card[i]>=2){
                    card[i]-=2;
                    if(huHelper(card,pairCount+1,tripletCount,targetTripletCount)){
                        return true;
                    }
                    card[i]+=2;
                }
            }  
            if(i<=27 && i%9<=7 && card[i]>=1 &&card[i+1]>=1&&card[i+2]>=1){
                card[i]-=1;
                card[i+1]-=1;
                card[i+2]-=1;
                if(huHelper(card, pairCount, tripletCount+1, targetTripletCount)){
                    return true;
                }
                card[i]+=1;
                card[i+1]+=1;
                card[i+2]+=1;
            }
        }
        return false;

	}
	public void performAction(Action action,Card dropCard) {
		System.out.println("Player "+this.getName()+" performed action: "+action.getType());
		switch (action.getType()) {
		case PONG:
			pong(dropCard);
			break;
		case KONG:
			kong(dropCard);
			break;
		case CHOW:
			chow(action.getOptionsList().get(0),dropCard);
			break;
		case MINGKONG:
			mingKong(dropCard);
			break;
		case UMKONG:
			umKong();
			break;
		case HU:
			hu(dropCard);
			break;
		default:
			break;
		}
	}

	private void chow(List<Card> chowOptions,Card dropCard) {
		List<Card> temp = new ArrayList<>(chowOptions);
		for(Card c:chowOptions){
			for(Card hc:handCards){
				if(hc.getRank()==c.getRank()){
					handCards.remove(hc);
					break;
				}
			}
		}
		this.handCards.removeAll(chowOptions);
		temp.add(dropCard);
		Collections.sort(temp);
		this.deskCards.add(new Combination(Combination.Type.CHOW, temp));
	}

	public void pong(Card dropCard) {
		List<Card> temp = handCards.stream().filter(c->c.getRank()==dropCard.getRank()).limit(2).collect(Collectors.toList());
		handCards.removeAll(temp);
		temp.add(dropCard);
		this.getDeskCards().add(new Combination(Combination.Type.PONG, temp));
	}	
	
	public void kong(Card dropCard) {
		List<Card> temp = handCards.stream().filter(c->c.getRank()==dropCard.getRank()).limit(3).collect(Collectors.toList());
		handCards.removeAll(temp);
		temp.add(dropCard);
		this.getDeskCards().add(new Combination(Combination.Type.KONG, temp));
	}

	public void hu(Card dropCard){
		handCards.add(dropCard);
		System.out.println(name+" Hu!! ||| "+listAllCards());
	}

	public void zimo(){
		System.out.println(name+" Zimo!! ||| "+listAllCards());
	}

	public void mingKong(Card dropCard){
		handCards.stream()
			.filter(c->c.getRank()==dropCard.getRank())
			.forEach(c->handCards.remove(c));
		deskCards.stream()
			.filter(c->c.getType()==Combination.Type.PONG)
			.filter(c->c.getCards().get(0).getRank()==dropCard.getRank())
			.map(c->c.getCards().add(dropCard));
		
	}

	private void umKong() {
		for(Card hand:handCards){
			if(handCards.stream().filter(c->c.getRank()==hand.getRank()).count()==4){
				List<Card> temp = handCards.stream().filter(c->c.getRank()==hand.getRank()).limit(4).collect(Collectors.toList());
				handCards.removeAll(temp);
				this.getDeskCards().add(new Combination(Combination.Type.KONG, temp));
				break;
			}
		}
	}

	public String listAllCardWithIndex() {
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
		if(!deskCards.isEmpty()) {
			result += " ||| "+ deskCards.stream().map(Combination::toString).collect(Collectors.joining(" , "));
		}
		return result;
	}
	public String allCardstoRank() {
		return handCards.stream()
				.map(Card::getRankStr)
				.collect(Collectors.joining(","));
	}

    public String listAllCards() {
        String result = "";
		result += handCards.stream()
				.map(Card::toString)
				.collect(Collectors.joining(","));
		if(!deskCards.isEmpty()) {
			result += " ||| "+deskCards.stream().map(Combination::toString).collect(Collectors.joining(","));
		}
		return result;
    }

	public boolean canMingKong(Card draw){
		for(Combination c:deskCards){
			if(c.getType()==Combination.Type.PONG){
				if(c.getCards().get(0).getRank()==draw.getRank()){
					return true;
				}
			}
		}
		return false;
	}

	public boolean canUmKong(){
		for(Card hand:handCards){
			if(handCards.stream().filter(c->c.getRank()==hand.getRank()).count()==4){
				return true;
			}
		}
		return false;
	}
}
