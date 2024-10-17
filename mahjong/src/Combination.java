
import java.util.List;
import java.util.stream.Collectors;


public class Combination {
    public enum Type{
        CHOW,PONG,KONG;
    }
    private Type type;
    private List<Card> cards;
    public Combination(Type type,List<Card> cards) {
        this.type = type;
        this.cards = cards;
    }
    public Type getType() {
        return type;
    }
    public List<Card> getCards() {
        return cards;
    }
    @Override
    public String toString(){
        return "["+cards.stream().map(Card::toString).collect(Collectors.joining("-"))+"]";
    }
}
