package utils;

import game.Card;

import java.util.List;

public class CardUtils {

	public static int calculatePoints(List<Card> cards){
		int result = 0;
		if (cards!=null){
			for (Card c : cards){
				result += c.getValue();
			}
		}
		return result;
	}
}
