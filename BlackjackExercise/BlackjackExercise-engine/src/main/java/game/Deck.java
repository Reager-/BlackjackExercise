package game;

import utils.RandomUtils;

import java.util.LinkedList;
import java.util.Queue;

public class Deck {

    private Queue<Card> deckOfCards;

    public Deck() {
        LinkedList<Card> list = new LinkedList<>();
        
        /*
         * Create all cards for each suit
         */
        
        for (CardSuit suit : CardSuit.values()) {
            list.add(new Card(CardType.Ace, suit, 1));
            list.add(new Card(CardType.Jack, suit, 10));
            list.add(new Card(CardType.Queen, suit, 10));
            list.add(new Card(CardType.King, suit, 10));
            for (int value = 2; value <= 10; value++) {
                list.add(new Card(CardType.Numeric, suit, value));
            }
        }
        list.addAll(list); // duplicate deck of cards
        RandomUtils.shuffle(list);
        this.deckOfCards = list;
    }

    public Queue<Card> getDeckOfCards() {
		return deckOfCards;
	}

	public void setDeckOfCards(Queue<Card> deckOfCards) {
		this.deckOfCards = deckOfCards;
	}

	public Card nextCard() {
        return this.deckOfCards.poll();
    }

	@Override
	public String toString() {
		return "Deck [deckOfCards=" + deckOfCards + "]";
	}
	
}