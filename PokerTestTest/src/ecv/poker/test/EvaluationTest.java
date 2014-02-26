package ecv.poker.test;

import java.util.List;

import android.util.Log;
import ecv.poker.card.Card;
import ecv.poker.card.Evaluator;
import ecv.poker.game.Game;
import ecv.poker.player.Player;
import junit.framework.TestCase;

public class EvaluationTest extends TestCase {

	private Game game;
	private Player player;

	protected void setUp() throws Exception {
		super.setUp();
		game = new Game();
		player = new Player(game);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		player.getHand().clear();
	}

	public void testHighCard() {
		int[] cardIds = new int[] { 202, 303, 404, 106, 307, 109, 208 };
		evaluateHand(cardIds, Evaluator.HIGH_CARD);
	}

	public void testOnePair() {
		int[] cardIds = new int[] { 202, 310, 307, 306, 311, 103, 210 };
		evaluateHand(cardIds, Evaluator.ONE_PAIR);
	}

	public void testTwoPair() {
		int[] cardIds = new int[] { 202, 310, 302, 306, 311, 110, 208 };
		evaluateHand(cardIds, Evaluator.TWO_PAIR);
	}

	public void testTrips() {
		int[] cardIds = new int[] { 202, 310, 302, 306, 311, 102, 208 };
		evaluateHand(cardIds, Evaluator.TRIPS);
	}

	public void testStraight() {
		int[] cardIds = new int[] { 113, 210, 302, 306, 311, 114, 312 };
		evaluateHand(cardIds, Evaluator.STRAIGHT);
	}

	public void testFlush() {
		int[] cardIds = new int[] { 313, 310, 302, 306, 311, 114, 308 };
		evaluateHand(cardIds, Evaluator.FLUSH);
	}

	public void testFullHouse() {
		int[] cardIds = new int[] { 110, 102, 210, 104, 310, 102, 404 };
		evaluateHand(cardIds, Evaluator.FULL_HOUSE);
	}

	public void testQuads() {
		int[] cardIds = new int[] { 110, 102, 210, 104, 310, 106, 410 };
		evaluateHand(cardIds, Evaluator.QUADS);
	}

	public void testStraightFlush() {
		int[] cardIds = new int[] { 114, 102, 103, 104, 105, 106, 107 };
		evaluateHand(cardIds, Evaluator.STRAIGHT_FLUSH);
	}

	private void evaluateHand(int[] cardIds, int expectedValue) {
		for (int i = 0; i < cardIds.length; i++) {
			player.drawCard(new Card(cardIds[i]));
		}
		List<Card> cards = player.getHand().getBestCards();
		int val = Evaluator.evaluateCards(cards);
		int further = Evaluator.evaluateFurther(cards, val);
		Log.d("PokerTest", val + ", " + further);
		assertEquals("Expected: " + expectedValue + ", actual: " + val,
				expectedValue, val);
	}

}
