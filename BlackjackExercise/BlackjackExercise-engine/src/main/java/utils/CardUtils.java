package utils;

import game.Card;

import java.util.List;

public class CardUtils {

	public static Integer calculatePoints(List<Card> cards){
		Integer result = 0;
		if (cards!=null){
			for (Card c : cards){
				result += c.getValue();
			}
		}
		return result;
	}
}
