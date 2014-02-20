package ecv.poker.card;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class to evaluate a player's hand.
 * Must have at least 2 cards in hand, no more than 5.
 * Use Hand.java's getBestCards
 * 
 * @author Evan
 *
 */
public class Evaluator {
	public static final int HIGH_CARD = 0,
			 ONE_PAIR = 1,
			 TWO_PAIR = 2,
			 TRIPS = 3,
			 STRAIGHT = 4,
			 FLUSH = 5,
			 FULL_HOUSE = 6,
			 QUADS = 7,
			 STRAIGHT_FLUSH = 8;
	
	/**
	 * Attempt to rank the the hand given available cards.
	 * 
	 * @param playerCards
	 * @param communityCards
	 * @return a[0] = int 0-8 representing type of hand (two pair, flush, etc),
	 * and a[1] = the additional info used to compare like hands
	 */
	public static int[] evaluateCards(List<Card> originalCards) {		
		// first sort cards in descending order
		// highest pairs will appear first and will save search time
		List<Card> cards = new LinkedList<Card>(originalCards);
		Collections.sort(cards);
		Collections.reverse(cards);
		
		int[] returnVal = new int[2];
		int tmp;
		
		// check if the cards meet criteria of a hand value
		if(0 < (tmp = getStraightFlush(cards))) {
			returnVal[0] = STRAIGHT_FLUSH;
			returnVal[1] = tmp;
		} else if(0 < (tmp = getQuads(cards))) {
			returnVal[0] = QUADS;
			returnVal[1] = tmp;
		} else if(0 < (tmp = getFullHouse(cards))) {
			returnVal[0] = FULL_HOUSE;
			returnVal[1] = tmp;
		} else if(0 < (tmp = getFlush(cards))) {
			returnVal[0] = FLUSH;
		} else if(0 < (tmp = getStraight(cards))) {
			returnVal[0] = STRAIGHT;
			returnVal[1] = tmp;
		} else if(0 < (tmp = getTrips(cards))) {
			returnVal[0] = TRIPS;
			returnVal[1] = tmp;
		} else if(0 < (tmp = getTwoPair(cards))) {
			returnVal[0] = TWO_PAIR;
			returnVal[1] = tmp;
		} else if(0 < (tmp = getPair(cards))) {
			returnVal[0] = ONE_PAIR;
			returnVal[1] = tmp;
		} else {
			returnVal[0] = HIGH_CARD;
			returnVal[1] = cards.get(0).getRank();
		}
		return returnVal;
	}
	
	/* -------------- 
	 * Methods to determine the poker hand.
	 * Return an int used to compare like hands,
	 * Or zero if hand is not what it is being checked for
	 * -------------- */
	
	// all cards have same suit
	// return high card in hand
	private static int getFlush(List<Card> cards) {
		if(cards.size() == 5) {
			int suit = cards.get(0).getSuit();
			for(int i = 0; i < cards.size(); i++) {
				if(cards.get(i).getSuit() != suit)
					return 0;
			}
			return cards.get(0).getRank();

		} else
			return 0;
	}
	
	// cards are in consecutive order (hand in descending order)
	// ace can be used as low card - A5432 is a straight
	// returns high card in straight
	private static int getStraight(List<Card> cards) {
		if(cards.size() == 5) {
			boolean isStraight = true;
			for(int i = 1; i < cards.size() && isStraight; i++) {
				if(cards.get(i).getRank() != cards.get(i - 1).getRank() - 1) {
					isStraight = false;
					// allow hand to start with A5xxx
					if(i == 1 && cards.get(i).getRank() == 5 &&
							cards.get(0).getRank() == 14)
						isStraight = true;
				}
			}
			if(isStraight)
				return cards.get(0).getRank();
		} 
		return 0;
	}
	
	// hand is a flush and a straight
	// return highest card in hand
	private static int getStraightFlush(List<Card> cards) {
		int straight = getStraight(cards);
		if(getFlush(cards) > 0 && straight > 0)
			return straight;
		else
			return 0;
	}
	
	// return rank of four of a kind
	private static int getQuads(List<Card> cards) {
		if(cards.size() >= 4) {
			// compare first 2 cards to next 3
			for(int i = 0; i < 2; i++) {
				boolean isQuads = true;
				for(int j = i+1; j < i+4; j++) {
					if(cards.get(i).getRank() != cards.get(j).getRank())
						isQuads = false;
				}
				if(isQuads)
					return cards.get(i).getRank();
			}
		}
		return 0;
	}
	
	// return rank of three of a kind
	private static int getTrips(List<Card> cards) {
		if(cards.size() >= 3) {
			// compare first 3 cards to next 2
			for(int i = 0; i < cards.size() - 2; i++) {
				boolean isTrips = true;
				for(int j = i + 1; j < i + 3; j++) {
					if(cards.get(i).getRank() != cards.get(j).getRank())
						isTrips = false;
				}
				if(isTrips) 
					return cards.get(i).getRank();
			}
		}
		return 0;
	}
	
	// return rank of pair
	private static int getPair(List<Card> cards) {
		for(int i = 1; i < cards.size(); i++) {
			if(cards.get(i-1).getRank() == cards.get(i).getRank()) 
				return cards.get(i).getRank();
		}
		return 0;
	}
	
	// get one pair and check for another
	// return int:
	// 	1000s and 100s = high pair value
	// 	10s and 1s = low pair value
	private static int getTwoPair(List<Card> cards) {
		if(cards.size() >= 4) {
			int highPair = getPair(cards);
			if(highPair > 0) {
				// can ignore the first 2 cards (would be highPair)
				for(int i = 3; i < cards.size(); i++) {
					if(cards.get(i-1).getRank() == cards.get(i).getRank()
							&& cards.get(i).getRank() != highPair) {
						int value = highPair * 100;
						value += cards.get(i).getRank();
						return value;
					}
				}
			} 
		}
		return 0;
	}
	
	// trips and pair (different)
	// return int:
	//  1000s and 100s = trip value
	//  10s and 1s = pair value
	private static int getFullHouse(List<Card> cards) {
		if(cards.size() == 5) {
			int trips = getTrips(cards);
			if(trips > 0) {
				int value = trips * 100;
				// find another pair
				boolean pairFound = false;
				for(int i = 1; i < cards.size() && !pairFound; i++) {
					if(cards.get(i-1).getRank() == cards.get(i).getRank() && 
							cards.get(i).getRank() != trips) {
						value += cards.get(i).getRank();
						pairFound = true;
					}
				}
				if(pairFound)
					return value;
			}
		}
		return 0;
	}
}
