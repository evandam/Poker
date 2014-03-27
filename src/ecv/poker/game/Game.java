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
	private int ante;	// TODO: implement blinds?

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
		myTurn = random.nextBoolean();
		ante = 5;	// arbitrary for now...
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
		// post antes
		pot = 0;
		user.bet(ante);
		bot.bet(ante);
		curBet = 0;
		if(!myTurn) 
			makeBotPlay();
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
		// delay for testing (terrible idea in UI thread)
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(curBet == 0)
			bot.check();
		else
			bot.call();
		
		myTurn = true;
		makeNextMove();
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
			curBet = 0;
			
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
	 * A round of betting is complete when the last action is a check or call. A
	 * new card needs to come out or its the end of the hand.
	 * 
	 * @return
	 */
	public boolean isBettingDone() {
		// a player calls another's bet, or they both check
		return prevAction == Action.BET && curAction == Action.CALL || 
				prevAction == Action.CHECK && curAction == Action.CHECK;
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
		if(user.getCards().size() > 0) {
			userCards = new ArrayList<Card>(user.getCards());
			userCards.addAll(communityCards);
		} else {
			winners.add(bot);
			return winners;
		} 
		// check if computer folded
		if(bot.getCards().size() > 0) {
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
	public void endHand() {
		List<Player> winners = getWinners();
		// split pot evenly amongst winners
		for (Player player : winners) {
			player.addChips(pot / winners.size());
		}
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
