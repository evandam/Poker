package ecv.poker.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ecv.poker.card.Card;
import ecv.poker.card.Evaluator;
import ecv.poker.player.AIPlayer;
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
	private Player user;
	private AIPlayer bot;
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
		bot = new AIPlayer(this);
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
		bot.makeMove();
		try {
			bot.getThread().join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	 * Reset the deck, clear players' hands, award chips to the winner(s)
	 * 
	 * @return message to alert user of outcome
	 */
	public String endHand() {
		// determine who won
		int userRank = Evaluator.evaluate(user.getCards(), communityCards);
		int botRank = Evaluator.evaluate(bot.getCards(), communityCards);
		
		String msg;
		if (userRank > botRank) {
			user.addChips(pot);
			msg = "You won " + pot + " chips!";
		} else if (userRank < botRank) {
			bot.addChips(pot);
			msg = "Computer won " + pot + " chips!";
		} else {
			user.addChips(pot / 2);
			bot.addChips(pot / 2);
			msg = "Split pot!";
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
}
