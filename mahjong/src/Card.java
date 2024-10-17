
import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class Card implements Comparable<Card>,Serializable{

	private static final long serialVersionUID = 2884460149315280672L;

	public enum Suit{
		萬(Numbers),
		索(Numbers),
		筒(Numbers),
		風(EnumSet.of(Card.Value.東,Card.Value.南,Card.Value.西,Card.Value.北)),
		中(EnumSet.of(Card.Value.中)),
		發(EnumSet.of(Card.Value.發)),
		白(EnumSet.of(Card.Value.白));
		private final Set<Value> validValues;
		private Suit(Set<Value> validValues) {
			this.validValues = validValues;
		}
		public boolean validValues(Value value) {
			return validValues.contains(value);
		}
	}
	
	public enum Value{
		一(1),二(2),三(3),四(4),五(5),六(6),七(7),八(8),九(9),東(28),南(29),西(30),北(31),中(32),發(33),白(34);
		private final int rank;
		private Value(int rank) {
			this.rank=rank;
		}
		public int getRank() {
			return rank;
		}
	}
	
	private static final Set<Value> Numbers = EnumSet.of(Card.Value.一,Card.Value.二,Card.Value.三,Card.Value.四,Card.Value.五,Card.Value.六,Card.Value.七,Card.Value.八,Card.Value.九);
	public static List<Value> getNumbers(){return Arrays.asList(Card.Value.一,Card.Value.二,Card.Value.三,Card.Value.四,Card.Value.五,Card.Value.六,Card.Value.七,Card.Value.八,Card.Value.九);}
	private final Suit suit;
	private final Value value;
	private final int rank;
	public Card(Suit suit, Value value) throws Exception {
		if(!suit.validValues(value)) {
			throw new Exception(String.format("Invalid value for %s",suit));
		}
		this.suit = suit;
		this.value = value;
		switch (suit) {
		case 萬:
			this.rank = value.getRank();
			break;
		case 索:
			this.rank = value.getRank()+9;
			break;
		case 筒:
			this.rank = value.getRank()+18;
			break;
		default:
			this.rank = value.getRank();
			break;
		}
	}
	
	public Suit getSuit() {
		return suit;
	}

	public Value getValue() {
		return value;
	}
	public int getRank() {
		return rank;
	}
	public String getRankStr() {
		return String.valueOf(rank);
	}
	@Override
	public String toString() {
		switch (suit){
		case 中:
			return String.format("%s",value);
			//return "Zhong";
		case 發:
			return String.format("%s",value);
			//return "Fa";
		case 白:
			return String.format("%s",value);
			//return "Bai";
		case 萬:
			return String.format("%dM",value.rank);
		case 索:
			return String.format("%ds",value.rank);
		case 筒:
			return String.format("%dT",value.rank);
//		case 風:
//			if(value==Value.東){
//				return "East";
//			}
//			else if(value==Value.南){
//				return "South";
//			}
//			else if(value==Value.西){
//				return "West";
//			}
//			else if(value==Value.北){
//				return "North";
//			}
		default:
			return String.format("%s%s",value,suit);
		}
	}

	public String toAsciiString(){
		String string = "";
		switch (suit){
		case 中:
			return String.format(".------.\n|%s---- |\n| (\\/) |\n| :\\/: |\n| ZHONG|\n`------'",value);
		case 發:
			return String.format(".------.\n|%s--- |\n| $\\/$ |\n| $\\$: |\n| '-FA'|\n`------'",value);
		case 白:
			return String.format(".------.\n|%s-- |\n| (\\/) |\n| :\\/: |\n| 'BAI'|\n`------'",value);
//		case 萬:
//			return String.format(".------.\n|%d.--. |\n| :/\\: |\n| (__) |\n| '--'M|\n`------'",value.rank);
//		case 索:
//			return String.format(".------.\n|%d.--. |\n| :/\\: |\n| :\\/: |\n| '--'S|\n`------'",value.rank);
//		case 筒:
//			return String.format(".------.\n|%d.--. |\n| (\\/) |\n| :\\/: |\n| '--'T|\n`------'",value.rank);
//		case 風:
//			if(value==Value.東){
//				return ".------.\n|EAST- |\n| &\\/& |\n| :\\/: |\n| '---'|\n`------'";
//			}
//			else if(value==Value.南){
//				return ".------.\n|SOUTH |\n| (\\/) |\n| :\\/: |\n| '---'|\n`------'";
//			}
//			else if(value==Value.西){
//				return ".------.\n|WEST- |\n| (\\/) |\n| :\\/: |\n| '---'|\n`------'";
//			}
//			else if(value==Value.北){
//				return ".------.\n|NORTH |\n| (\\/) |\n| :\\/: |\n| '---'|\n`------'";
//			}
		default:
			return String.format(".------.\n|%s.--. |\n| :/\\: |\n| (__) |\n| '--'%s|\n`------'",value,suit);
		}
	}

	@Override
	public int compareTo(Card o) {
		return this.rank-o.rank;
	}

}