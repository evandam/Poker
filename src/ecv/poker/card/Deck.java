package ecv.poker.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * List of standard 52 cards, along with
 * convenient methods to shuffle, deal, and add to the deck.
 * 
 * @author Evan
 */
public class Deck {

	private List<Card> cards = new ArrayList<Card>(52);

	public Deck() {
		for (int i = 1; i <= 4; i++) {
			for (int j = 2; j <= 14; j++) {
				int id = i * 100 + j;
				cards.add(new Card(id));
			}
		}
	}
	
	/**
	 *  
	 * @return list of all cards in deck
	 */
	public List<Card> getCards() {
		return cards;
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
	public Card deal() {
		// removing last element from arraylist is most efficient
		return cards.remove(cards.size() - 1);
	}

	/**
	 * Add list of cards to the deck
	 * 
	 * @param toAdd
	 */
	public void add(List<Card> toAdd) {
		cards.addAll(toAdd);
	}
}
