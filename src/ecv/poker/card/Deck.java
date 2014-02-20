package ecv.poker.card;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * List of 52 cards
 * Some functionality taken from Crazy Eights
 * 
 * @author Evan
 */
public class Deck {
	
	private List<Card> cards = new LinkedList<Card>();
	
	public Deck() {
		for (int i = 0; i < 4; i++) {
			for (int j = 102; j < 115; j++) {
				int id = j + (i*100);
				Card card = new Card(id);
				cards.add(card);
			}
		}
	}
	
	/**
	 * Shuffle the deck
	 */
	public void shuffle() {
		Collections.shuffle(cards);
	}
	
	/**
	 * @return Card from top of deck
	 */
	public Card dealCard() {
		return cards.remove(0);
	}
	
	/**
	 * Bring the cards from the hand back to the deck
	 * 
	 * @param myCards
	 * @param computerCards
	 * @param communityCards
	 */
	public void returnCards(List<Card> myCards, List<Card> computerCards, List<Card> communityCards) {
		cards.addAll(myCards);
		cards.addAll(computerCards);
		cards.addAll(communityCards);
	}
}
