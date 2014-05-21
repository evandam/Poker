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

	private float expectedValue;
	private boolean moveQueued;
	private Thread aiThread;
	private Handler handler;
	private int numSimulations;
	private float bluffFrequency;

	public AIPlayer(Game game, String name, int startingChips) {
		super(game, name, startingChips);
		numSimulations = game.getView().getSettings()
				.getInt("simulations", 500);
		bluffFrequency = game.getView().getSettings().getInt("bluff", 20) / 100f;
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
		synchronized (aiThread) {
			if (aiThread.isAlive())
				moveQueued = true;
			else
				doBestMove();
		}
	}

	private void doBestMove() {
		// determine how much to bet...try half of current pot (3:1 odds)
		// otherwise make it the min/max bet possible
		int betSize = getGame().getPot() / 2;
		if(betSize > getGame().getMaxBetAllowed())
			betSize = getGame().getMaxBetAllowed();
		else if(betSize < getGame().getMinBetAllowed())
			betSize = getGame().getMinBetAllowed();
		
		// consider amount needed to call bet and raise more
		int raiseSize = getGame().getCurBet();
		if(raiseSize + getGame().getCurBet() > getGame().getMaxBetAllowed())
			raiseSize = getGame().getMaxBetAllowed() - getGame().getCurBet();
			
		if (getGame().getCurBet() == 0) {
			// bet when better than 50% chance of winning
			if (expectedValue > 0.5 && betSize > 0) {
				bet(betSize);
			} else {
				// negative expectation, but betting as a bluff
				if (getGame().getRandom().nextFloat() < bluffFrequency)
					bet(betSize);
				else
					check();
			}
		} else {
			// PO = bet / (bet + pot)
			float potOdds = (float) getGame().getCurBet()
					/ (getGame().getCurBet() + getGame().getPot());

			// positive expectation (EV better than PO)
			if (expectedValue >= potOdds) {
				// "significantly" better EV to PO..or bluffing
				if (raiseSize > 0 && (expectedValue > 1.5 * potOdds
						|| getGame().getRandom().nextFloat() < bluffFrequency)) {
					raise(raiseSize);
				} else
					call();
			} else {
				// note: either bluff or fold with negative expectation, never flat call
				if (raiseSize > 0 && getGame().getRandom().nextFloat() < bluffFrequency)
					raise(raiseSize);
				else
					fold();
			}
		}
		getGame().setMyTurn(true);
		getGame().makeNextMove();
	}

	/**
	 * Run simulations do calculate the expected odds of winning the hand The
	 * ExpectedValue is a float between 0 and 1, with 1 being a guaranteed win.
	 */
	public void calculateExpectedValue() {
		Log.d("POKER", "calculating EV");
		aiThread = new AIThread();
		aiThread.start();
	}

	private class AIThread extends Thread {

		@Override
		public void run() {
			// make copies of all cards in play
			List<Card> deck = new ArrayList<Card>(getGame().getDeck());
			List<Card> community = new ArrayList<Card>(getGame()
					.getCommunityCards());
			List<Card> opponentCards = new ArrayList<Card>(getGame().getUser()
					.getCards());
			int communityCardsDealt = community.size();

			int wins = 0;
			// break if player folds -- ending hand earlier
			for (int i = 0; i < numSimulations && !getGame().isHandOver(); i++) {
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

			expectedValue = (float) wins / numSimulations;

			// If user made move while thread was running,
			// we need to respond once it is done.
			synchronized (this) {
				if (moveQueued) {
					moveQueued = false;
					// handler runs on main (UI) thread.
					// need to handle Toasts and invalidating.
					handler.post(new Runnable() {
						@Override
						public void run() {
							doBestMove();
							getGame().getView().invalidate();
						}
					});
				}
			}
		}
	}
}
