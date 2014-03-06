package ecv.poker.test;

import java.util.List;

import junit.framework.TestCase;
import ecv.poker.card.Card;
import ecv.poker.card.Evaluator;
import ecv.poker.game.Game;
import ecv.poker.player.Player;

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
		evaluateHand(cardIds, Evaluator.HIGH_CARD, 908070604);
	}

	public void testOnePair() {
		int[] cardIds = new int[] { 202, 310, 307, 306, 311, 103, 210 };
		evaluateHand(cardIds, Evaluator.ONE_PAIR, 10110706);
	}

	public void testTwoPair() {
		int[] cardIds = new int[] { 202, 310, 302, 306, 311, 110, 208 };
		evaluateHand(cardIds, Evaluator.TWO_PAIR, 100211);
	}

	public void testTrips() {
		int[] cardIds = new int[] { 202, 310, 302, 306, 311, 102, 208 };
		evaluateHand(cardIds, Evaluator.TRIPS, 21110);
	}

	public void testStraight() {
		int[] cardIds = new int[] { 113, 210, 302, 306, 311, 114, 312 };
		evaluateHand(cardIds, Evaluator.STRAIGHT, 14);
	}

	public void testFlush() {
		int[] cardIds = new int[] { 313, 310, 302, 306, 311, 114, 308 };
		evaluateHand(cardIds, Evaluator.FLUSH, 1311100806);
	}

	public void testFullHouse() {
		int[] cardIds = new int[] { 110, 102, 210, 104, 310, 102, 404 };
		evaluateHand(cardIds, Evaluator.FULL_HOUSE, 1004);
	}

	public void testQuads() {
		int[] cardIds = new int[] { 110, 102, 210, 104, 310, 106, 410 };
		evaluateHand(cardIds, Evaluator.QUADS, 1006);
	}

	public void testStraightFlush() {
		int[] cardIds = new int[] { 114, 102, 103, 104, 105, 106, 107 };
		evaluateHand(cardIds, Evaluator.STRAIGHT_FLUSH, 14);
	}

	private void evaluateHand(int[] cardIds, int expectedVal1, int expectedVal2) {
		for (int i = 0; i < cardIds.length; i++) {
			player.drawCard(new Card(cardIds[i]));
		}
		List<Card> cards = Evaluator.getBestCards(player.getHand());
		int[] val = Evaluator.evaluateCards(cards);
		assertEquals(expectedVal1, val[0]);
		assertEquals(expectedVal2, val[1]);
	}

}
