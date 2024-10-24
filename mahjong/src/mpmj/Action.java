package mpmj;
import java.io.Serializable;
import java.util.*;

public class Action implements Comparable<Action>,Serializable{
	static final long serialVersionUID = 3432317576314647476L;
	public enum Type{
		PASS,PONG,CHOW,KONG,HU,MINGKONG,UMKONG,ZIMO;
	}
	private Type type;
	private Player player;
	private int rank;
	private int order;
	private List<List<Card>> options;
	private boolean drawAction;
	private List<Object> huHands;
	public Action(Type type,Player player,int order,List<List<Card>> options,boolean drawAction,List<Object> huHands) {
		this.type = type;
		this.player = player;
		this.order = order;
		this.drawAction = drawAction;
		if(options!=null)
			this.options=options;
		if(huHands!=null)
			this.huHands=huHands;
		switch (type) {
		case HU:
			this.rank=4;
			break;
		case KONG:
			this.rank=3;
			break;
		case PONG:
			this.rank=2;
			break;
		case CHOW:
			this.rank=1;
			break;
		}
	}
	public Player getPlayer() {
		return player;
	}
	public Type getType() {
		return type;
	}
	public int getRank() {
		return rank;
	}
	public int getOrder() {
		return order;
	}
	public List<Object> getHuHands(){
		return huHands;
	}
	public List<List<Card>> getOptionsList() {
		return options;
	}
	public void setOptionsList(List<List<Card>> optionList) {
		this.options = optionList;
	}
	public void setDrawAction(boolean drawAction) {
		this.drawAction = drawAction;
	}
	public boolean isDrawAction() {
		return drawAction;
	}
	@Override
	public String toString() {
		return String.format("%s", type);
	}
	@Override
	public int compareTo(Action o) {
		if(o.rank==this.rank) {
			return this.order-o.order;
		}
		return this.rank - o.rank;
	}
}
