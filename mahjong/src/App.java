import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class App{
	  public static void main(String[] args) {
		try{
	    	Scanner in = new Scanner(System.in);
			Player p1 = new Player("A");
			Player p2 = new Player("B");
			Player p3 = new Player("C");
			Player p4 = new Player("D");
			List<Player> pList = new ArrayList<>();
			pList.add(p1);
			pList.add(p2);
			pList.add(p3);
			pList.add(p4);
		    Deck deck = new Deck(pList,in);
//		    System.out.println("Deck has "+deck.getRemainingCard() + " cards");
		    deck.shuffle();
//		    deck.giveCard(true,in);
		    deck.giveCard(true);
		    System.out.println("p1: "+p1.listCards());
		    System.out.println("p2: "+p2.listCards());
		    System.out.println("p3: "+p3.listCards());
		    System.out.println("p4: "+p4.listCards());
//		    System.out.println("Deck has "+deck.getRemainingCard() + " cards");
		    int round = 1;
			int i=1;
			boolean action = false;
		    while(deck.getRemainingCard()>0) {
				Player p = pList.get(i-1);
				clearScreen();
				if(action){
					action=false;
				}
				else{
					deck.draw(p,true,false);
					//System.out.println("Deck has "+deck.getRemainingCard() + " cards now");
				}
				System.out.print("Please input the index of card you want to drop: ");
				int index = in.nextInt();
				String seq = deck.drop(p, index);
				//System.out.println(seq);
				// for(Player player=actionP;player != null;){
				// 	System.out.println(String.format("%s round!  Your Cards: %s",player.getName(),player.listCards()));
				// 	System.out.print("Please input the index of card you want to drop: ");
				// 	index = in.nextInt();
				// 	player = deck.drop(player, index, in);
				// }
				if(seq.equals("")){//someone action
					if(i==4){
						System.out.println("-------------------------------------------------------------");
						i=1;
						System.out.println("Round: "+ ++round);
					}
					else{
						i++;
					}
				}
				else if(seq.split("%PARAM%")[0].equals("HU")){	//Hu
					System.out.println("-------------------------------------------------------------");
					System.out.println(String.format("Player %s WIN!!!",pList.get(Integer.parseInt(seq.split("%PARAM%")[1])-1).getName()));
					System.out.println("-------------------------------------------------------------");
					break;
				}
				else{
					i=Integer.parseInt(seq.split("%PARAM%")[1]);
					action = true;
				}
		    }
	    	if(deck.getRemainingCard()<=0)
	    		System.out.println("No Remaining Card on Deck!");
	    	in.close();
	  }catch(Exception e){
		System.out.println(e.getMessage());
	  }

	}
	public static void clearScreen(){
		System.out.print("\033[H\033[2J");   
		System.out.flush();   
	}
}