package ecv.poker.card;

import java.util.LinkedList;
import java.util.List;

/**
 * A Player's hand is made of 2 cards,
 * And has up to 5 community cards available to use. 
 * Hands are compared by traditional poker rules.
 * 
 * This class holds a list of cards, 
 * and allows two hands to be compared.
 * 
 * @author Evan
 */
public class Hand implements Comparable<Hand>{
			
	private List<Card> myCards;
	private List<Card> communityCards;	// reference to community cards for game
	
	public Hand(List<Card> community) {
		myCards = new LinkedList<Card>();
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
	 * If the hand is made of more than 5 cards,
	 * The best hand must be found
	 * 
	 * @return List of up to 5 cards with greatest value
	 */
	public List<Card> getBestCards() {
		LinkedList<Card> allCards = new LinkedList<Card>();
		allCards.addAll(communityCards);
		allCards.addAll(myCards);
		LinkedList<Card> bestCards = new LinkedList<Card>(allCards);
		
		// make a 5 card hand by removing all combinations of 2 cards
		if(allCards.size() > 5) {
			// remove a card from hand to evaluate 5 card hand
			for(int i = 0; i < 6; i++) {
				Card removedCard1 = allCards.remove(0);
				// evaluating 7 cards...need to remove another card
				if(allCards.size() > 5) {
					for(int j = i; j < 7; j++) {
						Card removedCard2 = allCards.remove(0);
						if(compareCardLists(allCards, bestCards) >= 0)
							bestCards = new LinkedList<Card>(allCards);
						allCards.add(removedCard2);
					}
				} else if(compareCardLists(allCards, bestCards) >= 0) {
					bestCards = new LinkedList<Card>(allCards);
				}
				allCards.add(removedCard1);	// put card back at end of list
			}
		} 
		return bestCards;
	}
	
	private static int compareCardLists(List<Card> one, List<Card> two) {
		int[] thisRank = Evaluator.evaluateCards(one);
		int[] thatRank = Evaluator.evaluateCards(two);
		
		if(thisRank[0] < thatRank[0])
			return -1;
		else if(thisRank[0] > thatRank[0])
			return 1;
		// both hands are the same type. need to compare further
		else {
			if(thisRank[1] < thatRank[1])
				return -1;
			else if(thisRank[1] > thatRank[1])
				return 1;
			// same pair, trips, kicker, etc. need to find next highest card
			else {
				for(int i = 0; i < one.size(); i++) {
					int thisCardRank = one.get(i).getRank();
					int thatCardRank = two.get(i).getRank();
					if(thisCardRank < thatCardRank)
						return -1;
					else if(thisCardRank > thatCardRank)
						return 1;
				}
			}
		}
		// fell through and hands are equivalent
		return 0;
	}

	@Override
	public int compareTo(Hand another) {
		return compareCardLists(this.getBestCards(), another.getBestCards());
	}
}
