package ecv.poker.card;

import java.util.ArrayList;
import java.util.List;

/**
 * A Player's hand is made of 2 cards, And has up to 5 community cards available
 * to use. Hands are compared by traditional poker rules.
 * 
 * This class holds a list of cards, and allows two hands to be compared.
 * 
 * @author Evan
 */
public class Hand implements Comparable<Hand> {

	private List<Card> holeCards;
	private List<Card> communityCards; // reference to community cards for game

	public Hand(List<Card> community) {
		holeCards = new ArrayList<Card>(2);
		communityCards = community;
	}

	public List<Card> getHoleCards() {
		return holeCards;
	}
	
	public List<Card> getCommunityCards() {
		return communityCards;
	}

	public void add(Card card) {
		holeCards.add(card);
	}

	public void clear() {
		holeCards.clear();
	}

	@Override
	public int compareTo(Hand another) {
		List<Card> thisCards = Evaluator.getBestCards(this);
		List<Card> thatCards = Evaluator.getBestCards(another);
		int thisRank = Evaluator.evaluateCards(thisCards);
		int thatRank = Evaluator.evaluateCards(thatCards);

		if (thisRank < thatRank)
			return -1;
		else if (thisRank > thatRank)
			return 1;
		// both hands are the same type. need to compare further
		else {
			int further1 = Evaluator.evaluateFurther(thisCards, thisRank);
			int further2 = Evaluator.evaluateFurther(thatCards, thatRank);
			if (further1 < further2)
				return -1;
			else if (further1 > further2)
				return 1;
			else
				return 0;
		}
	}
}
