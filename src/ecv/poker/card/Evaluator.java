package ecv.poker.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class to evaluate a player's hand. Must have at least 2 cards in
 * hand, no more than 5. Use Hand.java's getBestCards
 * 
 * @author Evan
 * 
 */
public class Evaluator {

	public static final int HIGH_CARD = 0, ONE_PAIR = 1, TWO_PAIR = 2,
			TRIPS = 3, STRAIGHT = 4, FLUSH = 5, FULL_HOUSE = 6, QUADS = 7,
			STRAIGHT_FLUSH = 8;

	/**
	 * If the hand is made of more than 5 cards, The best hand must be found.
	 * 
	 * @param hand
	 *            to get best cards for
	 * @return List of up to 5 cards with greatest value
	 */
	public static List<Card> getBestCards(Hand hand) {
		List<Card> allCards = new ArrayList<Card>();
		allCards.addAll(hand.getCommunityCards());
		allCards.addAll(hand.getHoleCards());
		int[] bestIndexes = new int[5];
		int[] bestVal = new int[] {0, 0};

		List<Card> combination = new ArrayList<Card>(5);
		while (combination.size() < 5)
			combination.add(null);
		// create each possible combination of 5 card hands and get the highest
		// ranked one
		for (int i = 0; i < allCards.size() - 4; i++) {
			combination.set(0, allCards.get(i));
			for (int j = i + 1; j < allCards.size() - 3; j++) {
				combination.set(1, allCards.get(j));
				for (int k = j + 1; k < allCards.size() - 2; k++) {
					combination.set(2, allCards.get(k));
					for (int l = k + 1; l < allCards.size() - 1; l++) {
						combination.set(3, allCards.get(l));
						for (int m = l + 1; m < allCards.size(); m++) {
							combination.set(4, allCards.get(m));
							int[] curVal = evaluateCards(combination);
							if ((curVal[0] > bestVal[0]) || (curVal[0] == bestVal[0] 
									&& curVal[1] > bestVal[1])) {
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
		for (int i = 0; i < bestIndexes.length; i++) {
			bestCards.add(allCards.get(bestIndexes[i]));
		}
		return bestCards;
	}

	/**
	 * Attempt to rank the hand.
	 * Hopefully this can be used to some extent during a game,
	 * where a full 5 cards may not be available.
	 * 
	 * @param playerCards
	 * @return int 0-8 representing type of hand (two pair, flush, etc),
	 */
	public static int[] evaluateCards(List<Card> c) {
		// first sort cards in descending order
		// highest pairs will appear first and will save search time
		List<Card> cards = new ArrayList<Card>(c);
		Collections.sort(cards);
		Collections.reverse(cards);

		// hand is straight, trips, pair, etc.
		int hand = 0;
		// value includes value of pair, kickers, etc.
		int handValue = 0;
		int tmpVal = 0;
		if((tmpVal = getStraightFlush(cards))  > 0) {
			hand = STRAIGHT_FLUSH;
			handValue = tmpVal;
		} else if((tmpVal = getQuads(cards)) > 0) {
			hand = QUADS;
			handValue = tmpVal;
		} else if((tmpVal = getFullHouse(cards)) > 0) {
			hand = FULL_HOUSE;
			handValue = tmpVal;
		} else if((tmpVal = getFlush(cards)) > 0) {
			hand = FLUSH;
			handValue = tmpVal;
		} else if((tmpVal = getStraight(cards)) > 0) {
			hand = STRAIGHT;
			handValue = tmpVal;
		} else if((tmpVal = getTrips(cards)) > 0) {
			hand = TRIPS;
			handValue = tmpVal;
		} else if((tmpVal = getTwoPair(cards)) > 0) {
			hand = TWO_PAIR;
			handValue = tmpVal;
		} else if((tmpVal = getPair(cards)) > 0) {
			hand = ONE_PAIR;
			handValue = tmpVal;
		} else {
			hand = HIGH_CARD;
			handValue = getHighCard(cards);
		}
		
		return new int[] {hand, handValue};
	}

	
	// just return int with cards in descending order
	private static int getHighCard(List<Card> cards) {
		int val = 0;
		for(int i = 0; i < cards.size(); i++) {
			val += cards.get(i).getRank() * Math.pow(100, cards.size() - 1 - i);
		}
		return val;
	}

	// return int with pair value and up to 3 kicker values (descending)
	private static int getPair(List<Card> cards) {
		for (int i = 1; i < cards.size(); i++) {
			int curRank = cards.get(i).getRank();
			// pair found. rest of cards are kickers
			if (cards.get(i - 1).getRank() == curRank) {
				List<Integer> kickers = new ArrayList<Integer>();
				for (int j = 1; j < cards.size() - 1; j++) {
					kickers.add(cards.get((i + j) % cards.size()).getRank());
				}
				int val = curRank * (int) Math.pow(100, 3);
				Collections.sort(kickers);
				for (int j = 0; j < kickers.size(); j++) {
					val += kickers.get(j) * Math.pow(100, j);
				}
				return val;
			}
		}
		
		return 0;
	}

	// return int with highest pair, lowest pair, and maybe a kicker
	private static int getTwoPair(List<Card> cards) {
		// parse out the rank of highest pair
		int pair1 = getPair(cards) / 1000000;
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
						return pair1 * 10000 + curRank * 100 + kicker;
					}
				}
			}
		}
		return 0;
	}

	// return int with value of trips and up to 2 kickers
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
				int val = curRank * 10000;
				Collections.sort(kickers);
				for(int j = 0; j < kickers.size(); j++) {
					val += kickers.get(j) * Math.pow(100, j);
				}
				return val;
			}
		}
		return 0;
	}
	
	// if its a straight, only need the highest card in hand
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
				return highCard;
			}
		} 
		return 0;
	}
	
	// return int containing all cards in hand in descending order or 0
	private static int getFlush(List<Card> cards) {
		if(cards.size() == 5) {
			int val = 0;
			for(int i = 0; i < 5; i++) {
				if(cards.get(i).getSuit() != cards.get(0).getSuit())
					return 0;
				else {
					// add value as kicker. remember this is descending order
					val += cards.get(i).getRank() * Math.pow(100, cards.size() - 1 - i);
				}
			}
			return val;
		}
		return 0;
	}
	
	// return the value of trips and the pair
	private static int getFullHouse(List<Card> cards) {
		// get rank of trips by integer division
		int trips = getTrips(cards) / 10000;
		if(trips != 0) {
			// now get the pair
			for(int i = 1; i < cards.size(); i++) {
				int curRank = cards.get(i).getRank();
				if(curRank == cards.get(i - 1).getRank() &&
						curRank != trips) {
					return trips * 100 + curRank;
				}
			}
		}
		return 0;
	}
	
	// return value of the quads and a kicker or 0
	private static int getQuads(List<Card> cards) {
		for(int i = 3; i < cards.size(); i++) {
			int curRank = cards.get(i).getRank();
			if(cards.get(i - 1).getRank() == curRank &&
					cards.get(i - 2).getRank() == curRank &&
					cards.get(i - 3).getRank() == curRank) {
				int val = curRank * 100;
				int kicker = cards.get((i + 1) % cards.size()).getRank();
				if(kicker != val)
					val += kicker;
				return val;
			}
		}
		return 0;
	}
	
	// only need to return the high card since consecutive ranks
	private static int getStraightFlush(List<Card> cards) {
		if(getStraight(cards) > 0 && getFlush(cards) > 0)
			return cards.get(0).getRank();
		else
			return 0;
	}
}
