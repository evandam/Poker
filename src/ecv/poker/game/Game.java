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
	private Action prevAction, curAction;

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
		// myTurn = random.nextBoolean();
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
	 * Deal next card if applicable, and make the bot play
	 */
	public void makeNextMove() {
		// end hand if either folds (since 2 player)
		if(curAction == Action.FOLD) 
			endHand();
		else {
			dealNextCard();
			if(!myTurn) 
				makeBotPlay();
		}
	}
	
	/**
	 * This is where AI logic will go.
	 * For now, always check/call when possible.
	 */
	public void makeBotPlay() {
		if(curBet == 0)
			bot.check();
		else
			bot.call();
		
		myTurn = true;
		makeNextMove();
	}

	/**
	 * A round of betting is complete when the last action is a check or call. A
	 * new card needs to come out or its the end of the hand.
	 * 
	 * @return
	 */
	public boolean isBettingDone() {
		// a player folds, a player calls another's bet, or they both check
		return curAction == Action.FOLD || curAction == Action.CALL
				|| curAction == Action.CHECK && prevAction == Action.CHECK;
	}

	/**
	 * Flop deals out 3 cards at same time,
	 * Turn and river only deal one
	 * End the hand if all 5 cards are already dealt
	 */
	public void dealNextCard() {
		if(isBettingDone()) {
			// starts a new round of betting, clear out previous actions
			prevAction = null;
			curAction = null;
			
			if (communityCards.size() < 3) {
				communityCards.add(deal());
				communityCards.add(deal());
				communityCards.add(deal());
			} else if(communityCards.size() < 5)
				communityCards.add(deal());
			else
				endHand();
		}
	}

	/**
	 * Deal out cards to players and start the round
	 */
	public void setupHand() {
		Collections.shuffle(deck, random);
		for (int i = 0; i < 2; i++) {
			user.getCards().add(deal());
			bot.getCards().add(deal());
		}
		prevAction = null;
		curAction = null;
		myTurn = true;	// TODO: handle bot going first
	}

	public Card deal() {
		return deck.remove(deck.size() - 1);
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
	 * List because there is a chance that it is a split pot.
	 * 
	 * @return list of players who won the pot.
	 */
	public List<Player> getWinners() {
		List<Player> winners = new ArrayList<Player>();

		List<Card> userCards = new ArrayList<Card>(user.getCards());
		userCards.addAll(communityCards);
		List<Card> botCards = new ArrayList<Card>(bot.getCards());
		botCards.addAll(communityCards);

		int userRank = Evaluator.evaluate(userCards);
		int botRank = Evaluator.evaluate(botCards);

		if (userRank >= botRank)
			winners.add(user);
		if (userRank <= botRank)
			winners.add(bot);
		return winners;
	}

	/**
	 * Reset the deck, clear players' hands, award chips to the winner(s), and
	 * reset the pot.
	 * 
	 * @return message to alert user of outcome
	 */
	public void endHand() {
		List<Player> winners = getWinners();
		for (Player player : winners) {
			player.addChips(pot / winners.size());
		}
		pot = 0;
		deck.addAll(user.getCards());
		deck.addAll(bot.getCards());
		deck.addAll(communityCards);
		user.getCards().clear();
		bot.getCards().clear();
		communityCards.clear();
		// as long as both players have chips, continue
		if(user.getChips() > 0 && bot.getChips() > 0)
			setupHand();
	}
}
