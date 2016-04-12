package Deck;
import game.Deck;

public class DeckTest {

	public static void main(String[] args) {
		Deck deck = new Deck();
		System.out.println(deck.toString());
		System.out.println(deck.getDeckOfCards().size());
		
	}

}