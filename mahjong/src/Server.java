
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Server {
    private List<Player> playerList = new ArrayList<>(4);
    private List<Socket> clientSockets = new ArrayList<>(4);
    private List<ObjectOutputStream> clientOutputStreams = new ArrayList<>(4);
    private static boolean serverOn = false;
    private int responseNeeded;
    private boolean actioned = false;
    private Deck deck;
    private int index = 0;
    private static boolean gameFinish = false;
    private List<Action> availableActions;
    private List<Action> availableDrawActions;
    private List<Action> confirmedActions;
    
    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        System.out.println("Opening Server");
        serverOn = true;
        try (ServerSocket serverSocket = new ServerSocket(8086)) {
            while (serverOn) {
                Socket socket = serverSocket.accept();
                addConnect(socket);
            }
        } catch (IOException e) {
            System.out.println("Error in starting up the server at localhost:");
            e.printStackTrace();
        }
    }

    private synchronized void addConnect(Socket clientSocket) {
        if (clientSockets.size() < 4) {
            try {
                clientSockets.add(clientSocket);
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                clientOutputStreams.add(oos);
                System.out.println("New Connection with: " + clientSocket.getRemoteSocketAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeConnect(Socket clientSocket) {
        if (clientSockets.contains(clientSocket)) {
            try {
                int index = clientSockets.indexOf(clientSocket);
                clientOutputStreams.remove(index);
                playerList.remove(index);
                clientSockets.remove(clientSocket);
                System.out.println("Remove Connection with: " + clientSocket.getRemoteSocketAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void sendMessage(Socket clientSocket, Message message, boolean printAll) {
        if (clientSockets.contains(clientSocket)) {
            if (printAll) {
                System.out.println(String.format("Sending Message[type=%s] to %s", message.getType(), message));
            }
            try {
                ObjectOutputStream oos = clientOutputStreams.get(clientSockets.indexOf(clientSocket));
                oos.writeObject(message);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void broadcastMessage(Message message) {
        System.out.println("Sending Message to All Clients: " + message);
        for (Socket clientSocket : clientSockets) {
            sendMessage(clientSocket, message, false);
        }
    }

    private synchronized void processCommand(Socket clientSocket, Message message) {
        //System.out.println("[Message Received] :" + message);
        switch (message.getType()) {
            case join:
                handleJoin(clientSocket, message);
                break;
            case drop:
                handleDrop(message);
                break;
            case confirmAction:
            	handleAction(message);
            	break;
            default:
                System.out.println("Unknown Type");
                break;
        }
    }

    private void performAction(List<Action> actionList){
        Action temp = actionList.stream().max(Action::compareTo).get();
        if(temp.getType()==Action.Type.PASS){
            actioned=false;
        }
        else{
            playerList.get(temp.getPlayer().getPlayerId()).performAction(temp,deck.getDropCard());
            broadcastMessage(new Message(Message.Type.perfromAction,temp.getPlayer().getPlayerId(),temp));
            if(temp.getType()==Action.Type.KONG){
                broadcastMessage(new Message(Message.Type.draw, temp.getPlayer().getPlayerId(), deck.draw(playerList.get(temp.getPlayer().getPlayerId()))));
            }
            actioned=true;
        }
    }

    private void handleAction(Message message) {
		Action action = (Action) message.getContent();
		// playerList.get(message.getPlayerId()).performAction(action,deck.getDropCard());
		// broadcastMessage(new Message(Message.Type.perfromAction,message.getPlayerId(),action));
        if(action.isDrawAction()){
            if(action.getType()==Action.Type.PASS)
                availableDrawActions.clear();
            else{
                availableDrawActions.remove(action);
                broadcastMessage(new Message(Message.Type.perfromAction,message.getPlayerId(),action));
                playerList.get(message.getPlayerId()).performAction(action,deck.getDropCard());
            }
        }
        else{
            confirmedActions.add(action);
            responseNeeded--;
            if(action.getType()!=Action.Type.PASS)
                index = action.getPlayer().getPlayerId();
        }
        if(action.getType()==Action.Type.PASS){
            actioned=false;
        }
        else if(action.getType()==Action.Type.HU||action.getType()==Action.Type.ZIMO){
            gameFinish=true;
        }
        else if(action.getType()==Action.Type.MINGKONG||action.getType()==Action.Type.UMKONG){
            broadcastMessage(new Message(Message.Type.draw, message.getPlayerId(), deck.draw(playerList.get(message.getPlayerId()))));
            actioned=true;
        }
        else{
            actioned=true;
        }
        synchronized (this) {
            notifyAll();
        }
	}

	private void handleJoin(Socket clientSocket, Message message) {
        Player tempPlayer = (Player) message.getContent();
        tempPlayer.setPlayerId(playerList.size());
        playerList.add(tempPlayer);
        sendMessage(clientSocket, new Message(Message.Type.playerList, -1, new ArrayList<>(playerList)), true);
        broadcastMessage(new Message(Message.Type.join, tempPlayer.getPlayerId(), tempPlayer));
        System.out.println(String.format("Server now has %d players", playerList.size()));
        if (playerList.size() == 4) {
            broadcastMessage(new Message(Message.Type.start, -1, "START"));
            System.out.println("Game is ready to start!");
            new Thread(new GameHandler()).start();
        }
    }

    private void handleDrop(Message message) {
        int index = (int) message.getContent();
        Card tempCard = playerList.get(message.getPlayerId()).drop(index);
        deck.setDroppCard(tempCard);
        notifyAll();
        String dropMessage = String.format("%s dropped %s!", playerList.get(message.getPlayerId()).getName(), tempCard);
        System.out.println(dropMessage);
        broadcastMessage(new Message(Message.Type.drop, message.getPlayerId(), tempCard));
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private ObjectInputStream ois;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                this.ois = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                while (true) {
                    Message message = (Message) ois.readObject();
                    System.out.println("Received Message from " + clientSocket.getRemoteSocketAddress() + ": " + message);
                    processCommand(clientSocket, message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                removeConnect(clientSocket);
            } finally {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class GameHandler implements Runnable {
        public void run() {
            startGame();
        }
    }

    private void startGame() {
        deck = new Deck(playerList);
        System.out.println("Players List:\n" + playerList.stream().map(Player::toString).collect(Collectors.joining("\n")));
        try {
            deck.giveCard(false);
            for (int i = 0; i < clientSockets.size(); i++) {
                sendMessage(clientSockets.get(i), new Message(Message.Type.bulkdraw, playerList.get(i).getPlayerId(), playerList.get(i).allCardstoRank()), true);
            }
            while (deck.getRemainingCard() > 0) {

                deck.listAllPlayerCard();

                Player playerOfRound = playerList.get(index);
                responseNeeded = 0;
                if(!actioned){
                    Card draw = deck.draw(playerOfRound);
                	broadcastMessage(new Message(Message.Type.draw, playerOfRound.getPlayerId(), draw));
                    System.out.println(availableDrawActions);
                    while(!(availableDrawActions = deck.checkDrawAction(draw,playerOfRound)).isEmpty()){
                        for(Action action:availableDrawActions){
                            System.out.printf("%s can %s! Waiting for their response...\n",playerOfRound.getName(),action.getType());
                        }
                        sendMessage(clientSockets.get(index),new Message(Message.Type.queryAction,index,availableDrawActions),true);
                        synchronized (this) {
                                System.out.printf("Waiting for %s respond...",playerOfRound.getName());
                                try {
                                    wait();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    e.printStackTrace();
                                }
                            }
                    }
                }
                if(gameFinish){
                    finishGame(playerOfRound);
                    break;
                }
                actioned=false;
                sendMessage(clientSockets.get(index), new Message(Message.Type.queryDrop,index,null), true);
                synchronized (this) {
                	System.out.println("Waiting for "+playerOfRound.getName()+" to drop...");
                    wait();
                }
                System.out.println("Checking Available Action with dropped card:" + deck.getDropCard());
                availableActions = deck.checkAction(playerOfRound);
                confirmedActions = new ArrayList<>();
                if(!availableActions.isEmpty()) {
                	responseNeeded = availableActions.stream().map(Action::getPlayer).collect(Collectors.toSet()).size();
                    for(Action action:availableActions) {
                    	System.out.printf("%s can %s! Waiting for their response...\n",action.getPlayer().getName(),action.getType());
                    }
                    broadcastMessage(new Message(Message.Type.queryAction,-1,availableActions));
                    synchronized (this) {
                        while(responseNeeded>0){
                            System.out.println("Waiting for all response...(Still need "+responseNeeded+" response)");
                            wait();
                        }
                    }
                    performAction(confirmedActions);
            	}
                if(gameFinish){
                    finishGame(playerOfRound);
                    break;
                }
                if(!actioned)
                	index = (index+1) %4;
            }
            System.out.println("Game Finished!");
        }catch (Exception e) {
            e.printStackTrace();
            serverOn = false;
        }
    }

    private void finishGame(Player p){
        broadcastMessage(new Message(Message.Type.gameFinish,-1,p));
        System.out.println(p.getName()+" wins!");
    }
}
