import java.util.EnumSet;
import java.util.Set;


public class Card implements Comparable<Card>{
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
	public final Suit suit;
	public final Value value;
	public final int rank;
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
	@Override
	public String toString() {
		switch (suit){
		case 中:
			return String.format("%s",value);
		case 發:
			return String.format("%s",value);
		case 白:
			return String.format("%s",value);
		default:
			return String.format("%s%s",value,suit);
		}
	}

	@Override
	public int compareTo(Card o) {
		return this.rank-o.rank;
	}
}
