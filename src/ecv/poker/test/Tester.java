package ecv.poker.test;

import java.util.List;

import android.util.Log;
import ecv.poker.card.Card;
import ecv.poker.card.Evaluator;
import ecv.poker.game.Game;
import ecv.poker.player.Player;

/**
 * Essentially a test suit
 * Since it was a pain testing if methods for
 * ranking hands worked....
 * 
 * @author Evan
 */
public class Tester {

	private static Game table = new Game();
	private static Player player = new Player(table);

	// run tests to check hands for expected values
	public static void testEvaluations() {
		
		int[] cardIds = new int[]{ 114, 102, 103, 104, 105, 106, 107 };
		testHand(cardIds, Evaluator.STRAIGHT_FLUSH);
		
		cardIds = new int[]{ 110, 102, 210, 104, 310, 106, 410};
		testHand(cardIds, Evaluator.QUADS);
		
		cardIds = new int[]{ 110, 102, 210, 104, 310, 102, 404};
		testHand(cardIds, Evaluator.FULL_HOUSE);
		
		cardIds = new int[]{ 313, 310, 302, 306, 311, 114, 308};
		testHand(cardIds, Evaluator.FLUSH);
		
		cardIds = new int[]{ 113, 210, 302, 306, 311, 114, 312};
		testHand(cardIds, Evaluator.STRAIGHT);
		
		cardIds = new int[]{ 202, 310, 302, 306, 311, 102, 208};
		testHand(cardIds, Evaluator.TRIPS);
		
		cardIds = new int[]{ 202, 310, 302, 306, 311, 110, 208};
		testHand(cardIds, Evaluator.TWO_PAIR);
		
		cardIds = new int[]{ 202, 310, 307, 306, 311, 103, 210};
		testHand(cardIds, Evaluator.ONE_PAIR);
		
		cardIds = new int[]{ 202, 303, 404, 106, 307, 109, 208};
		testHand(cardIds, Evaluator.HIGH_CARD);
	}
	
	private static void testHand(int[] cardIds, int expectedValue) {
		for(int i = 0; i < cardIds.length; i++) {
			player.drawCard(new Card(cardIds[i]));
		}
		List<Card> cards = player.getHand().getBestCards();
		int val = Evaluator.evaluateCards(cards);
		int further = Evaluator.evaluateFurther(cards, val);
		Log.d("MyApp", (expectedValue == val) + " " + val + ", " + further);
		player.getHand().clear();
	}
}
