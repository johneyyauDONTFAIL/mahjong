
import java.io.Serializable;

public class Message implements Serializable{

	private static final long serialVersionUID = -743204071278667518L;
	public enum Type{
		playerList,join,ready,start,bulkdraw,draw,queryDrop,drop,queryAction,confirmAction,perfromAction,gameFinish;
	}
	
	private final int playerId;
	
	private Object content;
	
	private final Type type;
	public Message(Type type,int playerId,Object content) {
		this.type = type;
		this.playerId = playerId;
		this.content = content;
	}
	
	public Type getType() {
		return type;
	}
	public Object getContent() {
		return content;
	}
	public int getPlayerId() {
		return playerId;
	}
	public String toString() {
		return String.format("[Type: %s, To: %d, Content: %s]", type,playerId,content==null?"":content.toString());
	}
}
