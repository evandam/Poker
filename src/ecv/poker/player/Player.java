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
	 * Put player's chips into pot if they have enough to cover the bet.
	 * 
	 * @param number
	 *            of chips to bet
	 */
	public void bet(int bet) {
		if (chips >= bet) {
			chips -= bet;
			game.addToPot(bet);
		}
	}

	/**
	 * Call the current bet. If it is more than the player has, put them all in.
	 * Note that a "check" is the equivalent of calling 0.
	 * @param bet
	 */
	public void call(int bet) {
		if(chips > bet) {
			game.addToPot(bet);
			chips -= bet;			
		} else {
			game.addToPot(chips);
			chips = 0;
		}
	}

	/**
	 * Throw away player's cards. Ends hand assuming 2 players
	 */
	public void fold() {
		cards.clear();
	}

	/**
	 * @return the player's hole cards
	 */
	public List<Card> getCards() {
		return cards;
	}
}
