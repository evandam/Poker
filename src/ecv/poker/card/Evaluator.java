package ecv.poker.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class to evaluate a player's hand. 
 * 
 * @author Evan
 * 
 */
public class Evaluator {

	public static final int HIGH_CARD = 0, ONE_PAIR = 1, TWO_PAIR = 2,
			TRIPS = 3, STRAIGHT = 4, FLUSH = 5, FULL_HOUSE = 6, QUADS = 7,
			STRAIGHT_FLUSH = 8;

	private static final int rank_pos = 0x100000;
	
	/**
	 * Get an evaluation of the cards that can be compared to others.
	 * The best combination of cards is found if more than 5 are in the list.
	 * The list can be less than 5, so this function can be used in the
	 * middle of a round.
	 * 
	 * The returned value is a hexadecimal value with 6 digits. 
	 * The most significant digit always corresponds to the class constants,
	 * indicating if the cards make a flush, straight, two-pair, etc.
	 * 
	 * Subsequent digits depend on what that previous digit is.
	 * They may indicate the rank of a pair, followed by kickers, 
	 * The rank that a straight goes up to, and so on.
	 * 
	 * @param playerCards
	 * @return an evaluation of the cards as an integer
	 */
	public static int evaluate(List<Card> cards) {
		// first sort cards in descending order
		// highest pairs will appear first and will save search time
		Collections.sort(cards);
		Collections.reverse(cards);
		if(cards.size() > 5) 
			cards = getBestCards(cards);
	
		int val = 0;
		if((val = getStraightFlush(cards))  > 0) {
			return val;
		} else if((val = getQuads(cards)) > 0) {
			return val;
		} else if((val = getFullHouse(cards)) > 0) {
			return val;
		} else if((val = getFlush(cards)) > 0) {
			return val;
		} else if((val = getStraight(cards)) > 0) {
			return val;
		} else if((val = getTrips(cards)) > 0) {
			return val;
		} else if((val = getTwoPair(cards)) > 0) {
			return val;
		} else if((val = getPair(cards)) > 0) {
			return val;
		} else {
			return getHighCard(cards);
		}
	}

	/**
	 * If the hand being evaluated is made of more than 5 cards, 
	 * The best hand must be found by checking each combination possible.
	 * 
	 * @param cards 
	 * @return List of up to 5 cards with greatest value
	 */
	private static List<Card> getBestCards(List<Card> cards) {
		int[] bestIndexes = new int[5];
		int bestVal = 0;

		List<Card> combination = new ArrayList<Card>(5);
		while (combination.size() < 5)
			combination.add(null);
		// create each possible combination of 5 card hands and get the highest
		// ranked one
		for (int i = 0; i < cards.size() - 4; i++) {
			combination.set(0, cards.get(i));
			for (int j = i + 1; j < cards.size() - 3; j++) {
				combination.set(1, cards.get(j));
				for (int k = j + 1; k < cards.size() - 2; k++) {
					combination.set(2, cards.get(k));
					for (int l = k + 1; l < cards.size() - 1; l++) {
						combination.set(3, cards.get(l));
						for (int m = l + 1; m < cards.size(); m++) {
							combination.set(4, cards.get(m));
							int curVal = evaluate(combination);
							if (curVal > bestVal) {
								bestVal = curVal;
								bestIndexes[0] = i;
								bestIndexes[1] = j;
								bestIndexes[2] = k;
								bestIndexes[3] = l;
								bestIndexes[4] = m;
							}
						}
					}
				}
			}
		}
		// compile the best list of cards and return
		List<Card> bestCards = new ArrayList<Card>(5);
		for (int i : bestIndexes) {
			bestCards.add(cards.get(i));
		}
		return bestCards;
	}

	/**
	 * 
	 * @param cards
	 * @return 0x0-----
	 * Where the 5 digits are the card ranks in descending order.
	 */
	private static int getHighCard(List<Card> cards) {
		int val = 0;
		for(int i = 0; i < cards.size(); i++) {
			val += cards.get(i).getRank() * Math.pow(16, cards.size() - 1 - i);
		}
		return val;
	}

	/**
	 * 
	 * @param cards
	 * @return 0x10----
	 * First digit is rank of pair, last 3 are kickers.
	 */
	private static int getPair(List<Card> cards) {
		for (int i = 1; i < cards.size(); i++) {
			int curRank = cards.get(i).getRank();
			// pair found. rest of cards are kickers
			if (cards.get(i - 1).getRank() == curRank) {
				List<Integer> kickers = new ArrayList<Integer>();
				for (int j = 1; j < cards.size() - 1; j++) {
					kickers.add(cards.get((i + j) % cards.size()).getRank());
				}
				int val = ONE_PAIR * rank_pos;
				val += curRank * 0x1000;	// 16^3
				Collections.sort(kickers);
				for (int j = 0; j < kickers.size(); j++) {
					val += kickers.get(j) * Math.pow(16, j);
				}
				return val;
			}
		}
		
		return 0;
	}

	/**
	 * 
	 * @param cards
	 * @return 0x200---
	 * First digit is high pair
	 * Second digit is low pair
	 * Last digit is the kicker
	 */
	private static int getTwoPair(List<Card> cards) {
		// parse out the rank of highest pair (4th digit)
		int pair1 = getPair(cards) % rank_pos / 0x1000;
		if(pair1 > 0) {
			for(int i = 1; i < cards.size(); i++) {
				int curRank = cards.get(i).getRank();
				if(curRank != pair1) {
					// found second pair
					if(cards.get(i - 1).getRank() == curRank) {
						// find the kicker
						int kicker = 0;
						for(Card c : cards) {
							if(c.getRank() != pair1 && c.getRank() != curRank)
								kicker = c.getRank();
						}
						int val = TWO_PAIR * rank_pos;
						val += pair1 * 0x100;
						val += curRank * 0x10;
						val += kicker;
						return val;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 
	 * @param cards
	 * @return 0x300---
	 * First digit is rank of three-of-a-kind
	 * Last two are kickers
	 */
	private static int getTrips(List<Card> cards) {
		for(int i = 2; i < cards.size(); i++) {
			int curRank = cards.get(i).getRank();
			// found three of a kind
			if(cards.get(i - 1).getRank() == curRank && 
					cards.get(i - 2).getRank() == curRank) {
				// find the kickers
				List<Integer> kickers = new ArrayList<Integer>();
				for(int j = 1; j < cards.size() - 2; j++) {
					kickers.add(cards.get((i + j) % cards.size()).getRank());
				}
				int val = TRIPS * rank_pos; 
				val += curRank * 0x100;
				Collections.sort(kickers);
				for(int j = 0; j < kickers.size(); j++) {
					val += kickers.get(j) * Math.pow(16, j);
				}
				return val;
			}
		}
		return 0;
	}
	
	/**
	 * 
	 * @param cards
	 * @return 0x40000-
	 * Where the last digit is the rank of the highest card in the hand
	 */
	private static int getStraight(List<Card> cards) {
		if(cards.size() == 5) {
			boolean isStraight = true;
			for(int i = 1; i < 5 && isStraight; i++) {
				int curRank = cards.get(i).getRank();
				if(cards.get(i - 1).getRank() != curRank + 1) {
					// allow ace to "wrap around" so A5432 is a straight
					if(!(i == 1 && curRank == 5 && cards.get(0).getRank() == 14))
						isStraight = false;
				}
			}
			if(isStraight) {
				int highCard = cards.get(0).getRank();
				// 5-high straight for A-5, not ace...
				if(cards.get(0).getRank() == 14 && cards.get(1).getRank() == 5)
					highCard = 5;
				int val = STRAIGHT * rank_pos;
				val += highCard;
				return val;
			}
		} 
		return 0;
	}
	
	/**
	 * 
	 * @param cards
	 * @return 0x5-----
	 * All card ranks in descending order
	 */
	private static int getFlush(List<Card> cards) {
		if(cards.size() == 5) {
			int val = 0;
			for(int i = 0; i < 5; i++) {
				if(cards.get(i).getSuit() != cards.get(0).getSuit())
					return 0;
				else {
					// add value as kicker. remember this is descending order
					val += cards.get(i).getRank() * Math.pow(16, cards.size() - 1 - i);
				}
			}
			val += FLUSH * rank_pos;
			return val;
		}
		return 0;
	}
	
	/**
	 * 
	 * @param cards
	 * @return 0x6000--
	 * First digit is rank of the trips
	 * Last digit is rank of the pair
	 */
	private static int getFullHouse(List<Card> cards) {
		// parse out rank of trips (3rd digit)
		int trips = getTrips(cards) % rank_pos / 0x100;
		if(trips != 0) {
			// now get the pair
			for(int i = 1; i < cards.size(); i++) {
				int curRank = cards.get(i).getRank();
				if(curRank == cards.get(i - 1).getRank() &&
						curRank != trips) {
					int val = FULL_HOUSE * rank_pos;
					val += trips * 0x10;
					val += curRank;
					return val;
				}
			}
		}
		return 0;
	}
	
	/**
	 * 
	 * @param cards
	 * @return 0x7000--
	 * First digit is rank of the four-of-a-kind
	 * Last digit is the kicker
	 */
	private static int getQuads(List<Card> cards) {
		for(int i = 3; i < cards.size(); i++) {
			int curRank = cards.get(i).getRank();
			if(cards.get(i - 1).getRank() == curRank &&
					cards.get(i - 2).getRank() == curRank &&
					cards.get(i - 3).getRank() == curRank) {
				int val = QUADS * rank_pos;
				val += curRank * 0x10;
				// kicker is the next card in the list
				int kicker = cards.get((i + 1) % cards.size()).getRank();
				if(kicker != val)
					val += kicker;
				return val;
			}
		}
		return 0;
	}
	
	/**
	 * 
	 * @param cards
	 * @return 0x80000-
	 * Last digit is the highest card in straight
	 */
	private static int getStraightFlush(List<Card> cards) {
		// parse out high card in straight (first digit)
		int straightVal = getStraight(cards) % rank_pos;
		if(straightVal > 0 && getFlush(cards) > 0) {
			int val = STRAIGHT_FLUSH * rank_pos;
			val += straightVal;
			return val;
		}
		else
			return 0;
	}
}
