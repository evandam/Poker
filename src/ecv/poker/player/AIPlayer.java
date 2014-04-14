package ecv.poker.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;
import ecv.poker.card.Card;
import ecv.poker.card.Evaluator;
import ecv.poker.game.Game;

/**
 * A player controlled by artifical intelligence.
 *
 * @author evan
 *
 */
public class AIPlayer extends Player {
	private Game game;
	private AIThread aiThread;
	
	public AIPlayer(Game game) {
		super(game);
		this.game = game;
		aiThread = new AIThread();
	}
	
	public Thread getThread() {
		return aiThread;
	}
	
	/**
	 * Run simulations in new thread to determine
	 * Probability of winning the hand, and act on that information.
	 */
	public void makeMove() {
		// check the state of the thread first to ensure only 1 is running at a time
		if(aiThread.getState() == Thread.State.NEW) {
			aiThread.start();
		} else if(aiThread.getState() == Thread.State.TERMINATED) {
			aiThread = new AIThread();
			aiThread.start();
		}
	}
	
	private class AIThread extends Thread {
		private List<Card> deck, community, opponentCards;
		private List<Card> myEval, opponentEval;
		int communityCardsDealt;
		@Override
		public void run() {
			// make copies of all cards in play
			deck = new ArrayList<Card>(game.getDeck());
			community = new ArrayList<Card>(game.getCommunityCards());
			opponentCards = new ArrayList<Card>(game.getUser().getCards());
			communityCardsDealt = community.size();
			myEval = new ArrayList<Card>();
			opponentEval = new ArrayList<Card>();
			
			int wins = 0;
			for(int i = 0; i < 100; i++) {
				setup();
				if(Evaluator.evaluate(myEval) >= Evaluator.evaluate(opponentEval))
					wins++;
				tearDown();
			}
			Log.d("POKER", wins + " WINS");
		}
		
		// shuffle, deal out opponent and community cards.
		// and combine hole cards and community for evaluation
		private void setup() {
			Collections.shuffle(deck);
			while(opponentCards.size() < 2)
				opponentCards.add(deck.remove(deck.size()-1));
			while(community.size() < 5)
				community.add(deck.remove(deck.size()-1));
			
			myEval.addAll(getCards());
			myEval.addAll(community);
			opponentEval.addAll(opponentCards);
			opponentEval.addAll(community);
		}
		
		// return cards to deck and clear out hands
		private void tearDown() {
			deck.addAll(opponentCards);
			opponentCards.clear();
			while(community.size() > communityCardsDealt)
				deck.add(community.remove(community.size()-1));
			myEval.clear();
			opponentEval.clear();
		}
	}

}
