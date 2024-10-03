import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


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
	public void draw(Player p,boolean msg) {
		Card card  = cards.remove(0);
		if(msg)
			System.out.println("Drawed: "+card.toString()+ "(" +card.getValue().getRank()+ ")");
		p.draw(card);
		Collections.sort(p.getHandCards());
		// if(checkMingGong(card)){
			
		// }
	}

	public void drawLast(Player p,boolean msg) {
		Card card  = cards.remove(cards.size()-1);
		if(msg)
			System.out.println("Drawed: "+card.toString()+ "(" +card.getValue().getRank()+ ")");
		p.draw(card);
		Collections.sort(p.getHandCards());
	}
	
	public void giveCard(boolean debug,Scanner in) throws Exception {
		if(debug){
			System.err.println("Please input card by rank line by line:");
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
					draw(p,false);
				}
			}
		}
	}
	
	public String drop(Player p,int index,Scanner in){
//		System.out.println("start droping!!!");
		int actionedPlayer = -1;
		String action = "";
		try{
			Card temp = p.drop(index);
			int seq = 1;
			for(Player o:players) {
				if(o==p){
					seq++;
					continue;
				}
				else if(action.equals("")){
					List<Card> pongList = checkPong(o.getHandCards(),temp);
					List<Card> upList = checkUp(o.getHandCards(),temp);
					if(checkHu(o.getAllCards(), temp)){
						System.out.println(String.format("Player %s can HU!!! Do you want to do so? (Y/N)", o.getName()));
						String response = in.next();
						if(response.equalsIgnoreCase("Y")) {
							//o.hu(pongList,temp);
							System.out.println("Player "+o.getName()+" HU!!!!");
							action = "HU";
							actionedPlayer = seq;
						}
					}
					if(pongList.size()==2) {						
						if(action.equals("")) {
							System.out.println(o.listCards());
							System.out.println(String.format("Player %s can PONG! Do you want to do so? (Y/N)", o.getName()));
							String response = in.next();
							if(response.equalsIgnoreCase("Y")) {
								o.pong(pongList,temp);
								System.out.println("Player "+o.getName()+" PONG!!!!");
								action ="PONG";
								actionedPlayer=seq;
							}
						}
					}
					else if(pongList.size()==3){						
						if(action.equals("")) {
							System.out.println(o.listCards());
							System.out.println(String.format("Player %s can GONG! Do you want to do so? (Y/N)", o.getName()));
							String response = in.next();
							if(response.equalsIgnoreCase("Y")) {
								o.gang(pongList,temp);
								drawLast(o,true);
								System.out.println("Player "+o.getName()+" GANG!!!!");
								action ="GONG";
								actionedPlayer=seq;
							}
						}
					}			
					if(upList.size()>=2) {						
						if(action.equals("")) {
							System.out.println(o.listCards());
							System.out.println(String.format("Player %s can UP! Do you want to do so?", o.getName()));
							String response = in.next();
							if(response.equalsIgnoreCase("Y")) {
								int j = 1;
								if(upList.size()>2){
									System.out.print("Available Options: ");
									for(int i=0;i<upList.size()-1;i++){
										System.out.print(String.format(" [%s,%s] (%d)",upList.get(i),upList.get(i+1),j++));
										i++;
										if(i==upList.size()-1){
											System.out.println();
										}
										else{
											System.out.print(" || ");
										}
									}
									System.out.println();
									String choose = in.next();
									if(Integer.parseInt(choose)>j){
										System.out.println("Invalid option!");
										continue;
									}
									else{
										o.up(Arrays.asList(upList.get(Integer.parseInt(choose)*2-1),upList.get(Integer.parseInt(choose)*2-1-1)), temp);
									}
								}
								else{
									o.up(upList,temp);
								}
								System.out.println("Player "+o.getName()+" UP!!!!");
								action ="UP";
								actionedPlayer=seq;
							}
						}
					}
				}
				seq++;
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		return actionedPlayer==-1?"": action+"%PARAM%"+actionedPlayer;
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
	
	private List<Card> checkUp(List<Card> cards,Card drop){
		Card.Suit dropSuit = drop.getSuit();
		
		List<Card> sameSuit = cards.stream().filter(c->c.getSuit().equals(dropSuit)&&Math.abs(c.getRank()-drop.getRank())<=2).toList();
		List<Card> possibleComb = new ArrayList<>();

		for(int i=0;i<sameSuit.size()-1;i++){
			for(int j=i+1;j<sameSuit.size();j++){
				if(isLink(Arrays.asList(sameSuit.get(i),sameSuit.get(j),drop))){
					possibleComb.add(sameSuit.get(i));
					possibleComb.add(sameSuit.get(j));
				}
			}
		}
		return possibleComb;
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
	
	private boolean checkHu(List<Card> cards,Card drop) {
		List<Card> tempCard = new ArrayList<>(cards);
		tempCard.add(drop);
		int[] cardsArr = new int[35];
		for(Card c:tempCard){
			cardsArr[c.getRank()]++;
		}
		return hu(cardsArr,0,0);
	}
	private static boolean hu(int[] card,int pairCount, int tripletCount){
        if(pairCount==1 && tripletCount==4){
            return true;
        }
        for(int i=0;i<card.length;i++){
            if(card[i]==0){
                continue;
            }

            if(card[i]>=3){
                card[i]-=3;
                if(hu(card,pairCount,tripletCount+1)){
                    return true;
                }
                card[i]+=3;
            }
            if(pairCount==0){
                if(card[i]>=2){
                    card[i]-=2;
                    if(hu(card,pairCount+1,tripletCount)){
                        return true;
                    }
                    card[i]+=2;
                }
            }  
            if(i<=27 && i%9<=7 && card[i]>=1 &&card[i+1]>=1&&card[i+2]>=1){
                card[i]-=1;
                card[i+1]-=1;
                card[i+2]-=1;
                if(hu(card, pairCount, tripletCount+1)){
                    return true;
                }
                card[i]+=1;
                card[i+1]+=1;
                card[i+2]+=1;
            }
        }
        return false;
    }

	// private boolean checkMingGong(Player p) {
	// 	p.get
	// }

}
