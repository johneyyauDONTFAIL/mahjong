package mpmj;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import seq.Fan;



public class Client{
	private boolean debug;
	private Socket socket;
	private ObjectOutputStream oos;
	private boolean joined = false,started = false;
	private List<Player> playerList;
	private Player player;
	Scanner in;
	private List<Card> cardsOnTable;
	private Card dropCard;
	private boolean gui;
	private ClientGUI clientGUI;
	public static void main(String[] args) {
		Client client = new Client(args[0],args.length>1?args[1]:"f",args.length>2?args[2]:"f");
	}
	
	public Client(String name,String debug,String gui) {
		try {
			this.socket = new Socket("127.0.0.1",8086);
			this.playerList = new ArrayList<>();
			oos = new ObjectOutputStream(socket.getOutputStream());
			//PrintWriter pw = new PrintWriter(oos);
			in = new Scanner(System.in);
			//System.out.println("Please input your name:");
			//String name = in.nextLine();
			this.player = new Player(name);
			this.debug = debug.equals("t");
			this.gui = gui.equals("t");
			System.out.println("Connected to the Server!");
			Thread serverHandler = new Thread(new ServerHandler(socket));
			serverHandler.start();
			this.sendMessage(new Message(Message.Type.join, -1, this.player));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void sendMessage(Message message) {
		try
		{
			oos.writeObject(message);
			oos.flush();
			if(debug)
				System.out.println("[MESSAGE SUCCESSFULLY SENT] "+ message.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("[MESSAGE SENT FAILED]");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private synchronized void processCommand(Socket clientSocket,Message message) {
		if(debug)
			System.out.println("[Message Received] :"+message);
		switch(message.getType()) {
		case playerList:
			List<Player> tempList = (List<Player>)message.getContent();
			this.playerList = new ArrayList<>(tempList);
			this.player = playerList.get(playerList.size()-1);
			break;
		case join:
			Player temp = (Player)message.getContent();
			if(message.getPlayerId()!=this.player.getPlayerId()) {
				this.playerList.add(temp);
				System.out.println(String.format("Player %s just joined! Room now have %d players.",temp.getName(),this.playerList.size()));
			}
			System.out.println("playerList: "+this.listPlayer());
			break;
		case start:
			started = true;
			this.startGame();
			if(gui)
				clientGUI = new ClientGUI(this);
			System.out.println("Game is ready to start!");
			System.out.println("Your Info:"+this.player.toString());
			break;
		case bulkdraw:
//			List<Card> data = (List<Card>)message.getContent();
			String data = (String)message.getContent();
			if(message.getPlayerId()==this.player.getPlayerId()) {
				this.player.draw(data);;
				System.out.println("Game Start! Your card: "+this.player.getHandCards().toString());
			}
			if(gui)
				clientGUI.updatePlayerCards();
			break;
		case draw:
			Card dataCard = (Card)message.getContent();			
			if(message.getPlayerId()==this.player.getPlayerId()) {
				this.player.draw(dataCard);
				System.out.println("Your Round! You Draw: "+dataCard);
				// System.out.print("Please input the card index you want to drop: ");
				// int index = in.nextInt() -1;
				// Card dropCard = this.player.drop(index);
				// sendMessage(new Message(Message.Type.drop,this.player.getPlayerId(),index));
			}
			else {
				System.out.println(this.playerList.get(message.getPlayerId()).getName()+" Round......");
			}
			break;
		case queryDrop:
			if(message.getPlayerId()==this.player.getPlayerId()) {
				System.out.println("Your Card: "+this.player.listAllCardWithIndex());
				System.out.print("Please input the card index you want to drop: ");
				int dropIndex = in.nextInt() -1;
				while(dropIndex>this.player.getHandCards().size() || dropIndex<0) {
					System.out.print("Invalid Input! Please input the card index you want to drop: ");
					dropIndex = in.nextInt() -1;
				}
				dropCard = this.player.drop(dropIndex);
				sendMessage(new Message(Message.Type.drop,this.player.getPlayerId(),dropIndex));
			}
				break;
		case drop:
			cardsOnTable.add((Card)message.getContent());
			dropCard = (Card)message.getContent();
			if(gui) {
				clientGUI.updateBoardCards();
				clientGUI.updatePlayerCards();
			}
			if(message.getPlayerId()==this.player.getPlayerId()) {
				break;
			}
			else {
				System.out.println(playerList.get(message.getPlayerId())+" dropped "+(Card)message.getContent());
			}
			break;
		case queryAction:
			List<Action> actionData = (List<Action>) message.getContent();
			List<Action> actions = actionData.stream().filter(a->a.getPlayer().getPlayerId()==this.player.getPlayerId()).toList();
			if(actions.size()==0) {
				System.out.println("Someone can perfrom action...");
			}
			else {
				int cnt = 1;
				List<String> options = actions.stream().map(Action::toString).toList();
				String optionStr = "";
				for(String o:options) {
					optionStr+=o+String.format("[%d]",cnt++);
				}
				System.out.println("Your Card: "+this.player.listAllCards());
				System.out.print("You can "+optionStr+". Select the option by index or type -1 to pass.");
				int index = in.nextInt();
				while(index>actions.size()){
					System.out.println("Invalid Option. Please input valid index.");
					index = in.nextInt();
				}
				if(index==-1) {
					sendMessage(new Message(Message.Type.confirmAction,this.player.getPlayerId(),new Action(Action.Type.PASS, this.player, index, null,actions.get(0).isDrawAction(),null)));
					break;
				}
				Action selectedAction = actions.get(index-1);
				if(selectedAction.getType()==Action.Type.CHOW) {
					if(selectedAction.getOptionsList().size()>1) {
						System.out.print("Available Options:");
						for(List<Card> chowOptions:selectedAction.getOptionsList()) {
							System.out.printf(" [%s] ",chowOptions.stream().map(Card::toString).collect(Collectors.joining(",")));
						}
						System.out.println();
						int option = in.nextInt();
						selectedAction.setOptionsList(Arrays.asList(selectedAction.getOptionsList().get(option)));
					}
				}
				sendMessage(new Message(Message.Type.confirmAction,this.player.getPlayerId(),selectedAction));
			}
			break;
		case perfromAction:
			Action action = (Action)message.getContent();
			if(action.getType()!=Action.Type.PASS) {
				this.playerList.get(message.getPlayerId()).performAction(action,dropCard);
				if((int)message.getPlayerId()!=this.player.getPlayerId()) {
					System.out.printf("%s %s!\n",playerList.get(message.getPlayerId()).getName(),action.getType());
					if(action.getType()==Action.Type.HU) {
						playerList.get(message.getPlayerId()).setHandCard((List<Card>)action.getHuHands().get(0));
						playerList.get(message.getPlayerId()).setDeskCard((List<Combination>)action.getHuHands().get(1));
					}
				}
				else {
					// System.out.println("Please select the card you want to drop: "+this.player.listAllCardWithIndex());
					// int index = in.nextInt()-1;
					// Card dropCard = this.player.drop(index);
					// sendMessage(new Message(Message.Type.drop,this.player.getPlayerId(),index));
				}
			}
			break;
		case gameFinish:
			started = false;
			break;
		default:
			break;
		}
	}

	private String listPlayer() {
		return playerList.stream().map(Player::toString).collect(Collectors.joining(","));
	}

	private class ServerHandler implements Runnable{
		private Socket socket;
		private ObjectInputStream ois;
		private ServerHandler(Socket socket) {
			try {
				this.socket = socket;
				this.ois = new ObjectInputStream(socket.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			Message message;
			try {
				while((message = (Message) this.ois.readObject()) !=null) {
					processCommand(socket, message);
				}
			}catch (Exception e) {
				System.out.println("Connection End.");
				in.close();
				e.printStackTrace();
			}
		}
	}
	

	private void startGame() {
		cardsOnTable = new ArrayList<>();
	}

    public Player getPlayer() {
        return this.player;
    }
	public List<Card> getCardsOnTable() {
		return this.cardsOnTable;
	}
}
