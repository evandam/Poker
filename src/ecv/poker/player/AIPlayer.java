package ecv.poker.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Handler;
import android.util.Log;
import ecv.poker.card.Card;
import ecv.poker.card.Evaluator;
import ecv.poker.game.Game;

/**
 * A player controlled by artificial intelligence.
 * 
 * @author evan
 * 
 */
public class AIPlayer extends Player {
	private Thread aiThread;
	private float expectedValue;
	private boolean moveQueued;
	private Game game;
	private Handler handler;

	public AIPlayer(Game game, String name) {
		super(game, name);
		this.game = game;
		aiThread = new AIThread();
		handler = new Handler();
	}
	
	public float getExpectedValue() {
		return expectedValue;
	}

	/**
	 * Run simulations in new thread to determine Probability of winning the
	 * hand, and act on that information.
	 */
	public void makeMove() {
		// if the thread hasn't finished calculating the value,
		// tell it to call doBestMove when it's done
		synchronized(aiThread) {
			if(aiThread.isAlive())
				moveQueued = true;
			else
				doBestMove();
		}
	}
	
	private void doBestMove() {
		if (game.getCurBet() > 0) {
			call();
		} else {
			check();
		}
		game.setMyTurn(true);
		game.makeNextMove();
	}
	
	/**
	 * Run simulations do calculate the expected odds of winning the hand
	 * The ExpectedValue is a float between 0 and 1, with 1 being a guaranteed win.
	 */
	public void calculateExpectedValue() {
		aiThread = new AIThread();
		aiThread.start();
	}

	private class AIThread extends Thread {
		
		private static final int NUM_SIMULATIONS = 500;
		
		@Override
		public void run() {
			Log.d("POKER", "Calculating EV");
			// make copies of all cards in play
			List<Card> deck = new ArrayList<Card>(game.getDeck());
			List<Card> community = new ArrayList<Card>(game.getCommunityCards());
			List<Card> opponentCards = new ArrayList<Card>(game.getUser().getCards());
			int communityCardsDealt = community.size();

			int wins = 0;
			for (int i = 0; i < NUM_SIMULATIONS; i++) {
				Collections.shuffle(deck);
				while (opponentCards.size() < 2)
					opponentCards.add(deck.remove(deck.size() - 1));
				while (community.size() < 5)
					community.add(deck.remove(deck.size() - 1));

				if (Evaluator.evaluate(getCards(), community) >= Evaluator
						.evaluate(opponentCards, community))
					wins++;

				deck.addAll(opponentCards);
				opponentCards.clear();
				while (community.size() > communityCardsDealt)
					deck.add(community.remove(community.size() - 1));
			}
			Log.d("POKER", wins + " WINS");
			expectedValue = (float) wins / NUM_SIMULATIONS;
			
			synchronized(this) {
				if(moveQueued) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							doBestMove();
							moveQueued = false;
							game.getView().invalidate();
						}
					});
				}
			}
		}
	}

}
