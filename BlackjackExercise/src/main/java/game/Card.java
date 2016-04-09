package game;

import java.io.Serializable;

public class Card implements Serializable{

	private static final long serialVersionUID = -7288871415262655062L;

	private CardType type;
    private CardSuit suit;
    private int value;

    public Card(CardType type, CardSuit suit, int value) {
        this.type = type;
        this.suit = suit;
        this.value = value;
    }

	public CardType getType() {
		return type;
	}

	public void setType(CardType type) {
		this.type = type;
	}

	public CardSuit getSuit() {
		return suit;
	}

	public void setSuit(CardSuit suit) {
		this.suit = suit;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Card [type=" + type + ", suit=" + suit + ", value=" + value
				+ "]";
	}

}