package ecv.poker.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ecv.poker.card.Card;
import ecv.poker.card.Evaluator;
import ecv.poker.player.Player;

/**
 * A representation of a game of poker. A game has a players, a deck of cards,
 * and community cards all players can use, and a pot that goes to the winning
 * hand
 * 
 * @author Evan
 */
public class Game {

	// private static final String TAG = "poker.game";

	/**
	 * An action a player can perform
	 */
	public static enum Action {
		FOLD, CHECK, CALL, BET, RAISE;
	}

	private Random random;
	private Player user, bot;
	private List<Card> deck, communityCards;
	private int pot;
	private int curBet;
	private boolean myTurn;
	private boolean handOver;
	private Action prevAction, curAction;
	private int ante; // TODO: implement blinds?

	// private AIThread aiThread;

	public Game() {
		random = new Random();
		user = new Player(this);
		bot = new Player(this);
		communityCards = new ArrayList<Card>(5);
		deck = new ArrayList<Card>(52);
		for (int i = 100; i <= 400; i += 100) {
			for (int j = 2; j <= 14; j++) {
				deck.add(new Card(i + j));
			}
		}
		myTurn = true;
		ante = 5; // arbitrary for now...ante 5, min bet 10
		handOver = false;
		// aiThread = new AIThread();
	}

	/**
	 * Deal out cards to players and start the round
	 */
	public void setupHand() {
		handOver = false;
		deck.addAll(user.getCards());
		deck.addAll(bot.getCards());
		deck.addAll(communityCards);
		user.getCards().clear();
		bot.getCards().clear();
		communityCards.clear();
		Collections.shuffle(deck, random);
		for (int i = 0; i < 2; i++) {
			user.getCards().add(deal());
			bot.getCards().add(deal());
		}
		// post antes
		pot = 0;
		user.bet(ante);
		bot.bet(ante);
		curBet = 0;

		if (!myTurn)
			makeBotPlay();
	}

	/**
	 * Deal next card if applicable, and make the bot play
	 * 
	 * @return message describing move - either bot's move or who won
	 */
	public String makeNextMove() {
		String msg;
		// end hand if either folds (since 2 player)
		if (curAction == Action.FOLD)
			msg = endHand();
		else {
			msg = dealNextCard();
			if (!myTurn)
				msg = makeBotPlay();
		}
		return msg;
	}

	/**
	 * This is where AI logic will go. For now, always check/call when possible.
	 * 
	 * @return string describing bot's move
	 */
	public String makeBotPlay() {
		// if(aiThread.getState() == Thread.State.NEW)
		// aiThread.start();
		// else if(aiThread.getState() == Thread.State.TERMINATED) {
		// aiThread = new AIThread();
		// aiThread.start();
		// }

		// no AI..bot doesn't bet/bluff/fold for now...
		String msg;
		if (curBet > 0) {
			msg = "Computer called " + curBet;
			bot.call();
		} else {
			msg = "Computer checked";
			bot.check();
		}
		myTurn = true;
		String nextMoveStr = makeNextMove();
		if (nextMoveStr != null)
			return nextMoveStr;
		else
			return msg;
	}

	/**
	 * Flop deals out 3 cards at same time, Turn and river only deal one End the
	 * hand if all 5 cards are already dealt
	 */
	public String dealNextCard() {
		if (isBettingDone()) {
			// starts a new round of betting, clear out previous actions
			prevAction = null;
			curAction = null;
			curBet = 0;

			if (communityCards.size() < 3) {
				communityCards.add(deal());
				communityCards.add(deal());
				communityCards.add(deal());
				return null;
			} else if (communityCards.size() < 5) {
				communityCards.add(deal());
				return null;
			} else
				return endHand();
		} else
			return null;
	}

	public boolean isHandOver() {
		return handOver;
	}

	/**
	 * A round of betting is complete when the last action is a check or call. A
	 * new card needs to come out or its the end of the hand.
	 * 
	 * @return
	 */
	public boolean isBettingDone() {
		// a player calls another's bet, or they both check
		return prevAction == Action.BET && curAction == Action.CALL
				|| prevAction == Action.CHECK && curAction == Action.CHECK;
	}

	/**
	 * List because there is a chance that it is a split pot.
	 * 
	 * @return list of players who won the pot.
	 */
	public List<Player> getWinners() {
		List<Player> winners = new ArrayList<Player>();
		List<Card> userCards, botCards;
		// check if player folded
		if (user.getCards().size() > 0) {
			userCards = new ArrayList<Card>(user.getCards());
			userCards.addAll(communityCards);
		} else {
			winners.add(bot);
			return winners;
		}
		// check if computer folded
		if (bot.getCards().size() > 0) {
			botCards = new ArrayList<Card>(bot.getCards());
			botCards.addAll(communityCards);
		} else {
			winners.add(user);
			return winners;
		}

		// players' hands need to be compared
		int userRank = Evaluator.evaluate(userCards);
		int botRank = Evaluator.evaluate(botCards);

		if (userRank >= botRank)
			winners.add(user);
		if (userRank <= botRank)
			winners.add(bot);

		return winners;
	}

	/**
	 * Reset the deck, clear players' hands, award chips to the winner(s)
	 * 
	 * @return message to alert user of outcome
	 */
	public String endHand() {
		List<Player> winners = getWinners();
		String msg = "";
		// split pot evenly amongst winners
		for (Player player : winners) {
			player.addChips(pot / winners.size());
			if (player == user)
				msg += "You won " + pot / winners.size() + " chips! ";
			else
				msg += "Computer won " + pot / winners.size() + " chips!";
		}
		handOver = true;
		return msg;
	}

	public Card deal() {
		return deck.remove(deck.size() - 1);
	}

	public boolean isMyTurn() {
		return myTurn;
	}

	public void setMyTurn(boolean myTurn) {
		this.myTurn = myTurn;
	}

	public List<Card> getDeck() {
		return deck;
	}

	public Player getUser() {
		return user;
	}

	public Player getBot() {
		return bot;
	}

	public int getAnte() {
		return ante;
	}

	public void setAnte(int ante) {
		this.ante = ante;
	}

	public List<Card> getCommunityCards() {
		return communityCards;
	}

	/**
	 * @return Size of the pot for the current hand
	 */
	public int getPot() {
		return pot;
	}

	public void addToPot(int bet) {
		pot += bet;
	}

	public int getCurBet() {
		return curBet;
	}

	public void setCurBet(int curBet) {
		this.curBet = curBet;
	}

	/**
	 * Set the current action, The old value of curAction is sent to prevAction
	 * 
	 * @param action
	 */
	public void setAction(Action action) {
		prevAction = curAction;
		curAction = action;
	}

	/**
	 * Run simulations of current hand in background until a "smart" move can be
	 * found TODO: Not ready for submission yet...
	 */
	/*
	 * private class AIThread extends Thread {
	 * 
	 * private static final int NUM_SIMULATIONS = 1000; private int wins;
	 * private List<Card> communityCopy, deckCopy, playerSimHand, botSimHand;
	 * 
	 * @Override public void run() { // setup current scenario wins = 0;
	 * deckCopy = new ArrayList<Card>(deck); playerSimHand = new
	 * ArrayList<Card>(); botSimHand = new ArrayList<Card>(); communityCopy =
	 * new ArrayList<Card>(communityCards); deckCopy.addAll(user.getCards());
	 * 
	 * // run the simulations! for(int i = 0; i < NUM_SIMULATIONS; i++) { //
	 * give player 2 random cards for(int j = 0; j < 2; j++)
	 * playerSimHand.add(deckCopy.remove(deckCopy.size()-1));
	 * 
	 * // deal out rest of community cards if necessary
	 * while(communityCopy.size() < 5)
	 * communityCopy.add(deckCopy.remove(deckCopy.size()-1));
	 * 
	 * // evaluate each hand with simulated community cards dealt
	 * playerSimHand.addAll(communityCopy); botSimHand.addAll(communityCopy);
	 * if(Evaluator.evaluate(botSimHand) >= Evaluator.evaluate(playerSimHand))
	 * wins++;
	 * 
	 * // reset scenario for next loop deckCopy.addAll(playerSimHand);
	 * deckCopy.addAll(botSimHand); deckCopy.addAll(communityCopy);
	 * 
	 * botSimHand.removeAll(communityCopy); playerSimHand.clear();
	 * communityCopy.retainAll(communityCards); }
	 * 
	 * // determine what to do... if(pot > 0) { // pot odds defined by current
	 * bet / total pot float potOdds = (float) curBet / pot; float simulatedOdds
	 * = (float) wins / NUM_SIMULATIONS; // TODO: synchronized blocks?
	 * if(simulatedOdds < potOdds) { // chances of winning less than value of
	 * pot...check, fold, or bluff (randomly) if(random.nextFloat() < 0.25) {
	 * if(curBet == 0) { bot.bet(curBet); Log.d(TAG, "ai bet " + curBet +
	 * " bluff"); } else { bot.raise(2 * curBet); Log.d(TAG, "ai raised " + 2 *
	 * curBet + " bluff"); } } else if(curBet > 0) { bot.fold(); Log.d(TAG,
	 * "ai folded"); } else { bot.check(); Log.d(TAG,
	 * "ai checked w/ negative odds"); } } // expected to win hand. should call
	 * or raise (split 50/50) else { if(random.nextBoolean()) { if(curBet > 0) {
	 * bot.call(); Log.d(TAG, "ai called w/ positive odds"); } else {
	 * bot.check(); Log.d(TAG, "ai checked w/ positive odds"); } } else {
	 * if(curBet > 0) { bot.raise(2 * curBet); Log.d(TAG,
	 * "ai raised with positive odds"); } else { bot.bet(pot / 2); Log.d(TAG,
	 * "ai bet with positive odds"); } } } } myTurn = true; makeNextMove(); } }
	 */
}
