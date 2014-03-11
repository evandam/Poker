package ecv.poker.test;

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
		player.getCards().clear();
	}

	public void testHighCard() {
		int[] cardIds = new int[] { 202, 303, 404, 106, 307, 109, 208 };
		int rank = 0x98764;
		evaluateHand(cardIds, rank);
	}

	public void testOnePair() {
		int[] cardIds = new int[] { 202, 310, 307, 306, 311, 103, 210 };
		int rank = 0x10AB76;
		evaluateHand(cardIds, rank);
	}

	public void testTwoPair() {
		int[] cardIds = new int[] { 202, 310, 302, 306, 311, 110, 208 };
		int rank = 0x200A2B;
		evaluateHand(cardIds, rank);
	}

	public void testTrips() {
		int[] cardIds = new int[] { 202, 310, 302, 306, 311, 102, 208 };
		int rank = 0x3002BA;
		evaluateHand(cardIds, rank);
	}

	public void testStraight() {
		int[] cardIds = new int[] { 113, 210, 302, 306, 311, 114, 312 };
		int rank = 0x40000E;
		evaluateHand(cardIds, rank);
	}

	public void testFlush() {
		int[] cardIds = new int[] { 313, 310, 302, 306, 311, 114, 308 };
		int rank = 0x5DBA86;
		evaluateHand(cardIds, rank);
	}

	public void testFullHouse() {
		int[] cardIds = new int[] { 110, 102, 210, 104, 310, 102, 404 };
		int rank = 0x6000A4;
		evaluateHand(cardIds, rank);
	}

	public void testQuads() {
		int[] cardIds = new int[] { 110, 102, 210, 104, 310, 106, 410 };
		int rank = 0x7000A6;
		evaluateHand(cardIds, rank);
	}

	public void testStraightFlush() {
		int[] cardIds = new int[] { 114, 102, 103, 104, 105, 106, 107 };
		int rank = 0x800007;
		evaluateHand(cardIds, rank);
	}

	private void evaluateHand(int[] cardIds, int expectedVal) {
		for (int i = 0; i < cardIds.length; i++) {
			player.getCards().add(new Card(cardIds[i]));
		}
		int val = Evaluator.evaluate(player.getCards());
		assertEquals(expectedVal, val);
	}

}
