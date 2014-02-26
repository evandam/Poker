package ecv.poker.card;

import java.util.Iterator;
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

	private List<Card> myCards;
	private List<Card> communityCards; // reference to community cards for game

	public Hand(List<Card> community) {
		myCards = new ArrayList<Card>();
		communityCards = community;
	}

	public List<Card> getCards() {
		return myCards;
	}

	public Card get(int i) {
		return myCards.get(i);
	}

	/**
	 * Add a card to the player's hand
	 * 
	 * @param card
	 */
	public void add(Card card) {
		myCards.add(card);
	}

	/**
	 * Remove the player's cards
	 */
	public void clear() {
		myCards.clear();
	}

	/**
	 * If the hand is made of more than 5 cards, The best hand must be found
	 * 
	 * @return List of up to 5 cards with greatest value
	 */
	public List<Card> getBestCards() {
		List<Card> allCards = new ArrayList<Card>(communityCards);
		allCards.addAll(myCards);

		int bestVal = -1, bestValFurther = -1;
		int bestCardId1 = -1, bestCardId2 = -1;

		int listSize = allCards.size();
		// make a 5 card hand by removing all combinations of 2 cards
		if (listSize > 5) {
			// remove a card from hand to evaluate 5 card hand
			for (int i = 0; i < 6; i++) {
				Card removedCard1 = allCards.remove(i);
				// evaluating 7 cards...need to remove another card
				if (listSize == 7) {
					for (int j = 0; j < 6; j++) {
						Card removedCard2 = allCards.remove(j);
						int curVal = Evaluator.evaluateCards(allCards);
						if (curVal > bestVal) {
							bestCardId1 = removedCard1.getId();
							bestCardId2 = removedCard2.getId();
							bestVal = curVal;
							bestValFurther = Evaluator.evaluateFurther(
									allCards, bestVal);
						} else if (curVal == bestVal) {
							int furtherEval = Evaluator.evaluateFurther(
									allCards, curVal);
							if (furtherEval > bestValFurther) {
								bestCardId1 = removedCard1.getId();
								bestCardId2 = removedCard2.getId();
								bestVal = curVal;
								bestValFurther = furtherEval;
							}
						}
						allCards.add(j, removedCard2);
					}
				}
				// evaluating a 6 card hand
				else {
					int curVal = Evaluator.evaluateCards(allCards);
					if (curVal > bestVal) {
						bestCardId1 = removedCard1.getId();
						bestVal = curVal;
						bestValFurther = Evaluator.evaluateFurther(allCards,
								bestVal);
					} else if (curVal == bestVal) {
						if (curVal == bestVal) {
							int furtherEval = Evaluator.evaluateFurther(
									allCards, curVal);
							if (furtherEval > bestValFurther) {
								bestCardId1 = removedCard1.getId();
								bestVal = curVal;
								bestValFurther = furtherEval;
							}
						}
					}
				}
				allCards.add(i, removedCard1); // put card back at end of list
			}
		}
		// remove the "worst" cards from the set
		Iterator<Card> iter = allCards.iterator();
		while (iter.hasNext()) {
			int nextId = iter.next().getId();
			if (nextId == bestCardId1 || nextId == bestCardId2)
				iter.remove();
		}
		return allCards;
	}

	@Override
	public int compareTo(Hand another) {
		List<Card> thisCards = new ArrayList<Card>(communityCards);
		List<Card> thatCards = new ArrayList<Card>(communityCards);
		thisCards.addAll(myCards);
		thatCards.addAll(another.getCards());
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
