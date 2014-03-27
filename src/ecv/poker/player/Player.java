package ecv.poker.player;

import java.util.ArrayList;
import java.util.List;

import ecv.poker.card.Card;
import ecv.poker.game.Game;

/*
 * TODO: abstract this to user and computer players?
 */
public class Player {
	private int chips;
	private List<Card> cards;
	private Game game;

	public Player(Game game) {
		this.game = game;
		chips = 1000; // or some default number?
		cards = new ArrayList<Card>(2);
	}

	/**
	 * @return Number of chips player has left to play with
	 */
	public int getChips() {
		return chips;
	}

	/**
	 * @param i
	 *            number of chips to be added or deducted from player
	 */
	public void addChips(int i) {
		chips += i;
	}
	
	/**
	 * No action. Nothing to call or bet
	 */
	public void check() {
		game.setAction(Game.Action.CHECK);
	}
	
	/**
	 * Call the current bet and deduct from chip stack
	 * @param bet
	 */
	public void call() {
		game.addToPot(game.getCurBet());
		chips -= game.getCurBet();	
		game.setAction(Game.Action.CALL);
		game.setCurBet(0);
	}

	/**
	 * Set the current bet, add to pot and deduct from chip stack
	 * @param bet
	 */
	public void bet(int bet) {
		chips -= bet;
		game.addToPot(bet);
		game.setCurBet(bet);
		game.setAction(Game.Action.BET);
	}
	
	/**
	 * Call the current bet and raise additional
	 * @param raise
	 */
	public void raise(int raise) {
		int total = game.getCurBet() + raise;
		chips -= total;
		game.addToPot(total);
		game.setCurBet(raise);
		game.setAction(Game.Action.RAISE);
	}

	/**
	 * Throw away player's cards.
	 */
	public void fold() {
		cards.clear();
		game.setAction(Game.Action.FOLD);
		game.setCurBet(0);
	}

	/**
	 * @return the player's hole cards
	 */
	public List<Card> getCards() {
		return cards;
	}
}
