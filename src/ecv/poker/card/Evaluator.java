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
	 * Attempt to rank the 5 card hand.
	 * 
	 * @param playerCards
	 * @return int 0-8 representing type of hand (two pair, flush, etc),
	 */
	public static int evaluateCards(List<Card> playerCards) {		
		// first sort cards in descending order
		// highest pairs will appear first and will save search time
		List<Card> cards = new LinkedList<Card>(playerCards);
		Collections.sort(cards);
		Collections.reverse(cards);
		int cardsSize = cards.size();	// only calculate once here...used a lot...
		
		int returnVal;
	
		// check if the cards meet criteria of a hand value
		if(isStraightFlush(cards, cardsSize)) {
			returnVal = STRAIGHT_FLUSH;
		} else if(isQuads(cards, cardsSize)) {
			returnVal = QUADS;
		} else if(isFullHouse(cards, cardsSize)) {
			returnVal = FULL_HOUSE;
		} else if(isFlush(cards, cardsSize)) {
			returnVal = FLUSH;
		} else if(isStraight(cards, cardsSize)) {
			returnVal = STRAIGHT;
		} else if(isTrips(cards, cardsSize)) {
			returnVal = TRIPS;
		} else if(isTwoPair(cards, cardsSize)) {
			returnVal = TWO_PAIR;
		} else if(isPair(cards, cardsSize)) {
			returnVal = ONE_PAIR;
		} else {
			returnVal = HIGH_CARD;
		}
		return returnVal;
	}
	
	/**
	 * This is necessary to compare hands that fall in the same category
	 * - like determining the higher straight, full house, etc.
	 * @param cards to evaluate
	 * @param value determined from evaluateCards - saves calculating twice
	 * @return
	 */
	public static int evaluateFurther(List<Card> playerCards, int value) {
		List<Card> cards = new LinkedList<Card>(playerCards);
		Collections.sort(cards);
		Collections.reverse(cards);		
		int cardsSize = cards.size();	// only calculate once

		int returnVal = 0;
		
		// flushes and no-hands may require evaluating each card in the hand
		if(value == HIGH_CARD || value == FLUSH) {
			// put each card in descending order in an int, each card getting 2 digits
			for(int i = 0; i < cardsSize; i++) {
				returnVal += cards.get(i).getRank() * Math.pow(100, 4 - i);
			}
		}
		// evaluate a pair, then 3 kickers
		else if(value == ONE_PAIR) {
			int pair = 0, kicker1 = 0, kicker2 = 0, kicker3 = 0;
			Card curCard;
			for(int i = 1; i < cardsSize; i++) {
				curCard = cards.get(i);
				if(curCard.getRank() == cards.get(i-1).getRank()) {
					pair = curCard.getRank();
					if(cardsSize > 2)
						kicker1 = cards.get((i + 1) % cardsSize).getRank();
					if(cardsSize > 3)
						kicker2 = cards.get((i + 2) % cardsSize).getRank();
					if(cardsSize > 4)
						kicker3 = cards.get((i + 3) % cardsSize).getRank();
				}
			}
			// determine ranking of kickers
			int bestKick1, bestKick2, bestKick3;
			if(kicker1 > kicker2 && kicker1 > kicker3) {
				bestKick1 = kicker1;
				if(kicker2 > kicker3) {
					bestKick2 = kicker2;
					bestKick3 = kicker3;
				} else {
					bestKick2 = kicker3;
					bestKick3 = kicker2;
				}
			} else if(kicker2 > kicker1 && kicker2 > kicker3) {
				bestKick1 = kicker2;
				if(kicker1 > kicker3) {
					bestKick2 = kicker1;
					bestKick3 = kicker3;
				} else {
					bestKick2 = kicker3;
					bestKick3 = kicker1;
				}
			} else {
				bestKick1 = kicker3;
				if(kicker1 > kicker2) {
					bestKick2 = kicker1;
					bestKick3 = kicker2;
				} else {
					bestKick2 = kicker2;
					bestKick3 = kicker1;
				}
			}
			returnVal = pair * 1000000 + bestKick1 * 10000 + bestKick2 * 100 + bestKick3;
		} 
		// high pair, then low pair, then kicker
		else if(value == TWO_PAIR) {
			int highPair = 0, lowPair = 0, kicker = 0;
			if(cards.get(0).getRank() == cards.get(1).getRank()) {
				highPair = cards.get(0).getRank();
				if(cards.get(2).getRank() == cards.get(3).getRank()) {
					lowPair = cards.get(2).getRank();
					if(cardsSize > 4)
						kicker = cards.get(4).getRank();
				} else {
					lowPair = cards.get(3).getRank();
					kicker = cards.get(2).getRank();
				}
			} else {
				highPair = cards.get(1).getRank();
				lowPair = cards.get(3).getRank();
				kicker = cards.get(0).getRank();
			}
			returnVal = highPair * 10000 + lowPair * 100 + kicker;
		}
		// trips are evaluated by the 3 cards, the last 2 are kickers
		// this also works for a full house, the last 2 happen to be the same
		else if(value == TRIPS || value == FULL_HOUSE) {
			int trips = 0, kicker1 = 0, kicker2 = 0;
			for(int i = 2; i < cardsSize; i++) {
				int curRank = cards.get(i).getRank();
				if(cards.get(i-1).getRank() == curRank && cards.get(i-2).getRank() == curRank) {
					trips = curRank;
					if(cardsSize > 3)
						kicker1 = cards.get((i + 1) % cardsSize).getRank();
					if(cardsSize > 4)
						kicker2 = cards.get((i + 2) % cardsSize).getRank();
				}
			}
			// determine best kicker
			int bestKicker1, bestKicker2;
			if(kicker1 > kicker2) {
				bestKicker1 = kicker1;
				bestKicker2 = kicker2;
			} else {
				bestKicker1 = kicker2;
				bestKicker2 = kicker1;
			}
			returnVal = trips * 10000 + bestKicker1 * 100 + bestKicker2;
		}
		// straights only require the high card to compare since they're sequential
		else if(value == STRAIGHT || value == STRAIGHT_FLUSH) {
			returnVal = cards.get(0).getRank();
		}
		// quads are first evaluated by the value of the 4 cards, then the last
		else if(value == QUADS) {
			int quadsIndex = cards.get(0).getRank() == cards.get(1).getRank() ? 0 : 1;
			int kicker = 0;
			if(cardsSize > 4) 
				kicker = cards.get((quadsIndex + 4) % cardsSize).getRank();
			returnVal = cards.get(quadsIndex).getRank() * 100 + kicker;
		}		
		return returnVal;
	}
		
	/* -------------- 
	 * Methods to determine the poker hand.
	 * -------------- */
	
	// two cards of same rank
	private static boolean isPair(List<Card> cards, int cardsSize) {
		int curRank;
		for(int i = 1; i < cardsSize; i++) {
			curRank = cards.get(i).getRank();
			if(cards.get(i-1).getRank() == curRank) 
				return true;
		}
		return false;
	}

	// two sets of two cards of same rank
	private static boolean isTwoPair(List<Card> cards, int cardsSize) {
		if(cardsSize >= 4) {
			boolean onePairFound = false;
			// can ignore the first 2 cards (would be highPair)
			for(int i = 1; i < cardsSize; i++) {
				if(cards.get(i-1).getRank() == cards.get(i).getRank()) {
					if(!onePairFound)
						onePairFound = true;
					else
						return true;
				}
			}
		} 
		return false;
	}

	// three of same rank
	private static boolean isTrips(List<Card> cards, int cardsSize) {
		if(cardsSize >= 3) {
			// compare first 3 cards to next 2
			int curI, curJ;
			for(int i = 0; i < cardsSize - 2; i++) {
				curI = cards.get(i).getRank();
				boolean isTrips = true;
				for(int j = i + 1; j < i + 3; j++) {
					curJ = cards.get(j).getRank();
					if(curI != curJ) 
						isTrips = false;
				}
				if(isTrips) {
					return true;
				}
			}
		}
		return false;
	}

	// cards are in consecutive order (hand in descending order)
	// ace can be used as low card - A5432 is a straight
	private static boolean isStraight(List<Card> cards, int cardsSize) {	
		if(cardsSize == 5) {
			boolean isStraight = true;
			int curRank;
			for(int i = 1; i < cardsSize && isStraight; i++) {
				curRank = cards.get(i).getRank();
				if(curRank + 1 != cards.get(i - 1).getRank()) {
					isStraight = false;
					// allow hand to start with A5xxx
					if(i == 1 && curRank == 5 && cards.get(0).getRank() == 14) {
						isStraight = true;
					}
				}
			}
			return isStraight;
		} else
			return false;
	}

	// all cards have same suit
	private static boolean isFlush(List<Card> cards, int cardsSize) {
		if(cardsSize == 5) {
			int suit = cards.get(0).getSuit();
			for(int i = 1; i < cardsSize; i++) {
				if(cards.get(i).getSuit() != suit)
					return false;
			}
			return true;
		}
		return false;
	}
	
	// trips and a pair
	private static boolean isFullHouse(List<Card> cards, int cardsSize) {
		if(cardsSize == 5) {
			// first two cards are a pair
			int firstRank = cards.get(0).getRank();
			if(firstRank == cards.get(1).getRank()) {
				// first three cards are same, last 2 must be equal
				if(firstRank == cards.get(2).getRank()) {
					return cards.get(3).getRank() == cards.get(4).getRank();
				} 
				// otherwise last 3 cards must be the same
				else {
					int secondRank = cards.get(2).getRank();
					return secondRank == cards.get(3).getRank() &&
							secondRank == cards.get(4).getRank();
				}
			}					
		} 
		return false;
	}

	// four of the same rank
	private static boolean isQuads(List<Card> cards, int cardsSize) {
		if(cardsSize >= 4) {
			int curI, curJ;
			// compare first 2 cards to next 3
			for(int i = 0; i < cardsSize - 3; i++) {
				curI = cards.get(i).getRank();
				boolean isQuads = true;
				for(int j = i+1; j < i+4; j++) {
					curJ = cards.get(j).getRank();
					if(curI != curJ) 
						isQuads = false;
				}
				if(isQuads)
					return true;
			}
		}
		return false;
	}

	// hand is a flush and a straight
	private static boolean isStraightFlush(List<Card> cards, int cardsSize) {
		return isFlush(cards, cardsSize) && isStraight(cards, cardsSize);
	}
}
