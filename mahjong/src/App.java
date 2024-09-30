import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


public class App{
	  public static void main(String[] args) {
			Player p1 = new Player("A");
			Player p2 = new Player("B");
			Player p3 = new Player("C");
			Player p4 = new Player("D");
			List<Player> pList = new ArrayList<>();
			pList.add(p1);
			pList.add(p2);
			pList.add(p3);
			pList.add(p4);
		    Deck deck = new Deck(pList);
		    System.out.println("Deck has "+deck.getRemainingCard() + " cards");
		    deck.shuffle();
		    deck.giveCard();
		    System.out.println("p1: "+p1.listCards());
		    System.out.println("p2: "+p2.listCards());
		    System.out.println("p3: "+p3.listCards());
		    System.out.println("p4: "+p4.listCards());
		    System.out.println("Deck has "+deck.getRemainingCard() + " cards");
		    int round = 1;
	    	Scanner in = new Scanner(System.in);
		    while(deck.getRemainingCard()>0) {
		    	System.out.println("Round: "+ round++);
		    	for(Player p:pList) {
		    		deck.draw(p,true);
		    		System.out.print(String.format("%s round! Your Cards: ",p.getName()));
		    		System.out.println(p.listCards());
			    	System.out.print("Please input the index of card you want to drop: ");
			    	int index = in.nextInt();
			    	deck.drop(p, index,in);
			    	System.out.println("-------------------------------------------------------------");
		    	}
		    }
	    	System.out.println("No Remaining Card on Deck!");
	    	in.close();
	  }
}